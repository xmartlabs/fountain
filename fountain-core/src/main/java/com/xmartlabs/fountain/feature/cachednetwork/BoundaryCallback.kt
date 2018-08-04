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
import com.xmartlabs.fountain.adapter.NetworkResultListener
import java.util.concurrent.Executor

internal class BoundaryCallback<NetworkValue, DataSourceValue, NetworkResponse : ListResponse<out NetworkValue>>(
    private val networkDataSourceAdapter: NetworkDataSourceAdapter<NetworkResponse>,
    private val cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
    private val pagedListConfig: PagedList.Config,
    private val ioServiceExecutor: Executor,
    private val ioDatabaseExecutor: Executor,
    private val firstPage: Int
) : PagedList.BoundaryCallback<DataSourceValue>() {
  private var isLoadingInitialData = false
  private var page = firstPage
  var helper = PagingRequestHelper(ioServiceExecutor)
  val networkState = MutableLiveData<NetworkState>()
  val networkStateListener = PagingRequestHelper.Listener { report ->
    networkState.postValue(report.createNetworkState())
  }

  init {
    ioServiceExecutor.execute {
      helper.addListener(networkStateListener)
      synchronized(this) {
        isLoadingInitialData = true
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
          val requestedPages = pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
          networkDataSourceAdapter.pageFetcher.fetchPage(page = page, pageSize = pagedListConfig.initialLoadSizeHint,
              networkResultListener = createWebserviceListener(it, requestedPages, true))
        }
      }
    }
  }

  // ignored, since we are requesting the first page in the init method.
  override fun onZeroItemsLoaded() {}

  @MainThread
  override fun onItemAtEndLoaded(itemAtEnd: DataSourceValue) {
    ioServiceExecutor.execute {
      if (networkDataSourceAdapter.canFetch(page = page, pageSize = pagedListConfig.pageSize)) {
        synchronized(this) {
          if (!isLoadingInitialData) {
            helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
              networkDataSourceAdapter.pageFetcher.fetchPage(page = page, pageSize = pagedListConfig.pageSize,
                  networkResultListener = createWebserviceListener(it, 1))
            }
          }
        }
      }
    }
  }

  // ignored, since we only ever append to what's in the DB
  override fun onItemAtFrontLoaded(itemAtFront: DataSourceValue) {}

  @AnyThread
  fun resetData(): LiveData<NetworkState> {
    val resetNetworkState = MutableLiveData<NetworkState>()
    ioServiceExecutor.execute {
      synchronized(this) {
        if (!isLoadingInitialData) {
          isLoadingInitialData = true
          resetNetworkState.postValue(NetworkState.LOADING)
          networkDataSourceAdapter.pageFetcher.fetchPage(page = firstPage,
              pageSize = pagedListConfig.initialLoadSizeHint,
              networkResultListener = object : NetworkResultListener<NetworkResponse> {
                override fun onSuccess(response: NetworkResponse) {
                  ioDatabaseExecutor.execute {
                    @Suppress("TooGenericExceptionCaught")
                    try {
                      cachedDataSourceAdapter.runInTransaction {
                        cachedDataSourceAdapter.dropEntities()
                        cachedDataSourceAdapter.saveEntities(response.getElements())
                      }
                      page = firstPage + pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
                      onInitialDataLoaded()
                      helper.removeListener(networkStateListener)
                      helper = PagingRequestHelper(ioServiceExecutor)
                      helper.addListener(networkStateListener)
                      resetNetworkState.postValue(NetworkState.LOADED)
                    } catch (throwable: Throwable) {
                      onError(throwable)
                    }
                  }
                }

                override fun onError(t: Throwable) {
                  onInitialDataLoaded()
                  resetNetworkState.postValue(NetworkState.error(t))
                }
              })
        } else {
          resetNetworkState.postValue(NetworkState.error(IllegalStateException("The first page cannot be fetched")))
        }
      }
    }
    return resetNetworkState
  }

  private fun onInitialDataLoaded() {
    synchronized(this) {
      isLoadingInitialData = false
    }
  }

  private fun createWebserviceListener(
      callback: PagingRequestHelper.Request.Callback,
      requestedPages: Int,
      initialData: Boolean = false) = object : NetworkResultListener<NetworkResponse> {
    override fun onSuccess(response: NetworkResponse) {
      ioDatabaseExecutor.execute {
        @Suppress("TooGenericExceptionCaught")
        try {
          cachedDataSourceAdapter.runInTransaction {
            if (initialData) {
              cachedDataSourceAdapter.dropEntities()
            }
            cachedDataSourceAdapter.saveEntities(response.getElements())
          }
          page += requestedPages
          callback.recordSuccess()
          if (initialData) {
            onInitialDataLoaded()
          }
        } catch (throwable: Throwable) {
          onError(throwable)
        }
      }
    }

    override fun onError(t: Throwable) {
      if (initialData) {
        onInitialDataLoaded()
      }
      callback.recordFailure(t)
    }
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
