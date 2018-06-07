package com.xmartlabs.xlpagingbypagenumber.feature.cachednetwork

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import android.support.annotation.AnyThread
import android.support.annotation.MainThread
import com.xmartlabs.xlpagingbypagenumber.NetworkState
import com.xmartlabs.xlpagingbypagenumber.common.observeOn
import com.xmartlabs.xlpagingbypagenumber.common.subscribeOn
import com.xmartlabs.xlpagingbypagenumber.fetcher.PagingHandler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor

internal class BoundaryCallback<T, ServiceResponse>(private val pageFetcher: PagingHandler<out ServiceResponse>,
                                                    private val dataSourceEntityHandler: DataSourceEntityHandler<ServiceResponse>,
                                                    private val pagedListConfig: PagedList.Config,
                                                    private val ioServiceExecutor: Executor,
                                                    private val ioDatabaseExecutor: Executor,
                                                    private val firstPage: Int
) : PagedList.BoundaryCallback<T>() {
  private var isLoadingInitialData = false
  private var page = firstPage
  var helper = PagingRequestHelper(ioServiceExecutor)
  val networkState = MutableLiveData<NetworkState>()
  val networkStateListener = PagingRequestHelper.Listener { report -> networkState.postValue(report.createNetworkState()) }

  init {
    helper.addListener(networkStateListener)
    synchronized(this) {
      isLoadingInitialData = true
      helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
        pageFetcher.fetchPage(page = page, pageSize = pagedListConfig.initialLoadSizeHint)
            .createWebserviceCallback(it, pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize, true)
      }
    }
  }

  // ignored, since we are requesting the first page in the init method.
  override fun onZeroItemsLoaded() {}

  @MainThread
  override fun onItemAtEndLoaded(itemAtEnd: T) {
      if (pageFetcher.canFetch(page = page, pageSize = pagedListConfig.pageSize)) {
        synchronized(this) {
        if (!isLoadingInitialData) {
          helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            pageFetcher.fetchPage(page = page, pageSize = pagedListConfig.pageSize)
                .createWebserviceCallback(it, 1)
          }
        }
      }
    }
  }

  // ignored, since we only ever append to what's in the DB
  override fun onItemAtFrontLoaded(itemAtFront: T) {}

  @AnyThread
  fun resetData(): LiveData<NetworkState> {
    val networkState = MutableLiveData<NetworkState>()
    synchronized(this) {
      if (!isLoadingInitialData) {
        isLoadingInitialData = true
        networkState.postValue(NetworkState.LOADING)
        pageFetcher.fetchPage(page = firstPage, pageSize = pagedListConfig.initialLoadSizeHint)
            .subscribeOn(ioServiceExecutor)
            .observeOn(ioDatabaseExecutor)
            .subscribe(object : SingleObserver<ServiceResponse> {
              override fun onSuccess(t: ServiceResponse) {
                page = firstPage + pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
                dataSourceEntityHandler.runInTransaction {
                  dataSourceEntityHandler.dropEntities()
                  dataSourceEntityHandler.saveEntities(t)
                }
                onInitialDataLoaded()
                helper.removeListener(networkStateListener)
                helper = PagingRequestHelper(ioServiceExecutor)
                helper.addListener(networkStateListener)
                networkState.postValue(NetworkState.LOADED)
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(e: Throwable) {
                onInitialDataLoaded()
                networkState.postValue(NetworkState.error(e))
              }
            })
      } else {
        networkState.postValue(NetworkState.error(IllegalStateException("The first page cannot be fetched")))
      }
    }
    return networkState
  }

  private fun onInitialDataLoaded() {
    synchronized(this) {
      isLoadingInitialData = false
    }
  }

  private fun Single<out ServiceResponse>.createWebserviceCallback(callback: PagingRequestHelper.Request.Callback,
                                                                   requestedPages: Int,
                                                                   initialData: Boolean = false) {
    this
        .subscribeOn(ioServiceExecutor)
        .observeOn(ioDatabaseExecutor)
        .subscribe(object : SingleObserver<ServiceResponse> {
          override fun onSuccess(data: ServiceResponse) {
            page += requestedPages
            dataSourceEntityHandler.runInTransaction {
              if (initialData) {
                dataSourceEntityHandler.dropEntities()
              }
              dataSourceEntityHandler.saveEntities(data)
            }
            if (initialData) {
              onInitialDataLoaded()
            }
            callback.recordSuccess()
          }

          override fun onSubscribe(d: Disposable) {}

          override fun onError(t: Throwable) {
            if (initialData) {
              onInitialDataLoaded()
            }
            callback.recordFailure(t)
          }
        })
  }

  private fun PagingRequestHelper.StatusReport.getError(): Throwable = PagingRequestHelper.RequestType.values()
      .mapNotNull { getErrorFor(it) }
      .first()

  private fun PagingRequestHelper.StatusReport.createNetworkState()
      : NetworkState {
    return when {
      hasRunning() -> NetworkState.LOADING
      hasError() -> NetworkState.error(getError())
      else -> NetworkState.LOADED
    }
  }
}
