package com.xmartlabs.fountain.feature.cachednetwork

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import android.support.annotation.AnyThread
import android.support.annotation.MainThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.common.observeOn
import com.xmartlabs.fountain.common.subscribeOn
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor

internal class BoundaryCallback<NetworkValue, DataSourceValue, ServiceResponse : ListResponse<NetworkValue>>(
    private val networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>,
    private val cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
    private val pagedListConfig: PagedList.Config,
    private val ioServiceExecutor: Executor,
    private val ioDatabaseExecutor: Executor,
    private val firstPage: Int
) : PagedList.BoundaryCallback<DataSourceValue>() {
  private var isLoadingInitialData = false
  private var page = firstPage
  var helper = PagingRequestHelper(ioServiceExecutor)
  val networkState = MutableLiveData<NetworkState<ServiceResponse>>()

  init {
    synchronized(this) {
      isLoadingInitialData = true
      helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
        networkState.postValue(NetworkState.Loading(page))
        networkDataSourceAdapter.fetchPage(page = page, pageSize = pagedListConfig.initialLoadSizeHint)
            .createWebserviceCallback(it, pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize, true)
      }
    }
  }

  // ignored, since we are requesting the first page in the init method.
  override fun onZeroItemsLoaded() {}

  @MainThread
  override fun onItemAtEndLoaded(itemAtEnd: DataSourceValue) {
    if (networkDataSourceAdapter.canFetch(page = page, pageSize = pagedListConfig.pageSize)) {
      synchronized(this) {
        if (!isLoadingInitialData) {
          helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            networkState.postValue(NetworkState.Loading(page))
            networkDataSourceAdapter.fetchPage(page = page, pageSize = pagedListConfig.pageSize)
                .createWebserviceCallback(it, 1)
          }
        }
      }
    }
  }

  // ignored, since we only ever append to what's in the DB
  override fun onItemAtFrontLoaded(itemAtFront: DataSourceValue) {}

  @AnyThread
  fun resetData(): LiveData<NetworkState<ServiceResponse>> {
    val resetNetworkState = MutableLiveData<NetworkState<ServiceResponse>>()
    synchronized(this) {
      if (!isLoadingInitialData) {
        isLoadingInitialData = true
        resetNetworkState.postValue(NetworkState.Loading(firstPage))
        networkDataSourceAdapter.fetchPage(page = firstPage, pageSize = pagedListConfig.initialLoadSizeHint)
            .subscribeOn(ioServiceExecutor)
            .observeOn(ioDatabaseExecutor)
            .subscribe(object : SingleObserver<ServiceResponse> {
              override fun onSuccess(serviceResponse: ServiceResponse) {
                @Suppress("TooGenericExceptionCaught")
                try {
                  cachedDataSourceAdapter.runInTransaction {
                    cachedDataSourceAdapter.dropEntities()
                    cachedDataSourceAdapter.saveEntities(serviceResponse.getElements())
                  }
                  page = firstPage + pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
                  onInitialDataLoaded()
                  helper = PagingRequestHelper(ioServiceExecutor)
                  resetNetworkState.postValue(NetworkState.Success(serviceResponse))
                } catch (throwable: Throwable) {
                  onError(throwable)
                }
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(e: Throwable) {
                onInitialDataLoaded()
                resetNetworkState.postValue(NetworkState.Error(e))
              }
            })
      } else {
        resetNetworkState.postValue(NetworkState.Error(IllegalStateException("The first page cannot be fetched")))
      }
    }
    return resetNetworkState
  }

  private fun onInitialDataLoaded() {
    synchronized(this) {
      isLoadingInitialData = false
    }
  }

  private fun Single<out ServiceResponse>.createWebserviceCallback(
      callback: PagingRequestHelper.Request.Callback,
      requestedPages: Int,
      initialData: Boolean = false) {
    this
        .subscribeOn(ioServiceExecutor)
        .observeOn(ioDatabaseExecutor)
        .subscribe(object : SingleObserver<ServiceResponse> {
          override fun onSuccess(response: ServiceResponse) {
            @Suppress("TooGenericExceptionCaught")
            try {
              cachedDataSourceAdapter.runInTransaction {
                if (initialData) {
                  cachedDataSourceAdapter.dropEntities()
                }
                cachedDataSourceAdapter.saveEntities(response.getElements())
              }
              page += requestedPages
              networkState.postValue(NetworkState.Success(response))
              callback.recordSuccess()
              if (initialData) {
                onInitialDataLoaded()
              }
            } catch (throwable: Throwable) {
              onError(throwable)
              networkState.postValue(NetworkState.Error(throwable))
            }
          }

          override fun onSubscribe(d: Disposable) {}

          override fun onError(t: Throwable) {
            if (initialData) {
              onInitialDataLoaded()
            }
            networkState.postValue(NetworkState.Error(t))
            callback.recordFailure(t)
          }
        })
  }

  private fun PagingRequestHelper.StatusReport.getError(): Throwable = PagingRequestHelper.RequestType.values()
      .mapNotNull { getErrorFor(it) }
      .first()
}
