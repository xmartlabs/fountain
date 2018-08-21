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

  init {
    ioServiceExecutor.execute {
      synchronized(this) {
        isLoadingInitialData = true
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
          val requestedPages = pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
          val loadingState = createInitialLoadingState()
          networkState.postValue(loadingState)
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
              val loadingState = createLoadingState(page, page + 1, pagedListConfig.pageSize)
              networkState.postValue(loadingState)
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

          val loadingState = createInitialLoadingState()
          resetNetworkState.postValue(loadingState)
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
                      helper = PagingRequestHelper(ioServiceExecutor)
                      resetNetworkState.postValue(
                          NetworkState.Loaded(
                              firstPage, pagedListConfig.initialLoadSizeHint, true, isLastPage(page + 1)
                          )
                      )
                    } catch (throwable: Throwable) {
                      onError(throwable)
                    }
                  }
                }

                override fun onError(t: Throwable) {
                  onInitialDataLoaded()
                  resetNetworkState.postValue(createInitialLoadingErrorState(t))
                }
              })
        } else {
          resetNetworkState.postValue(
              createInitialLoadingErrorState(IllegalStateException("The first page cannot be fetched"))
          )
        }
      }
    }
    return resetNetworkState
  }

  private fun createInitialLoadingErrorState(throwable: Throwable) =
      NetworkState.Error(throwable, firstPage, pagedListConfig.initialLoadSizeHint, true, isLastPage(page + 1))

  private fun createLoadingState(page: Int, nextPage: Int, pageSize: Int): NetworkState.Loading {
    return NetworkState.Loading(page, pageSize, page == firstPage, isLastPage(nextPage))
  }

  private fun createInitialLoadingState(): NetworkState.Loading {
    val nextPage = firstPage + 1 + pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
    return createLoadingState(firstPage, nextPage, pagedListConfig.initialLoadSizeHint)
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
        val requestedPage = page
        val isLastPage = isLastPage(page + requestedPages + 1)
        val pageSize = requestedPages * pagedListConfig.pageSize
        @Suppress("TooGenericExceptionCaught")
        try {
          cachedDataSourceAdapter.runInTransaction {
            if (initialData) {
              cachedDataSourceAdapter.dropEntities()
            }
            cachedDataSourceAdapter.saveEntities(response.getElements())
          }
          networkState.postValue(NetworkState.Loaded(page, pageSize, page == firstPage, isLastPage))
          page += requestedPages
          callback.recordSuccess()
          if (initialData) {
            onInitialDataLoaded()
          }
        } catch (throwable: Throwable) {
          onError(throwable)
          networkState.postValue(
              NetworkState.Error(throwable, requestedPage, pageSize, requestedPage == firstPage, isLastPage)
          )
        }
      }
    }

    override fun onError(t: Throwable) {
      if (initialData) {
        onInitialDataLoaded()
      }
      val isLastPage = isLastPage(page + requestedPages + 1)
      networkState.postValue(
          NetworkState.Error(t, page, requestedPages * pagedListConfig.pageSize, page == firstPage, isLastPage)
      )
      callback.recordFailure(t)
    }
  }

  private fun isLastPage(nextPage: Int) =
      !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = pagedListConfig.pageSize)
}
