package com.xmartlabs.fountain.feature.network

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.arch.paging.PagedList
import android.support.annotation.AnyThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import java.util.concurrent.Executor

internal class NetworkPagedDataSource<T, ServiceResponse : ListResponse<out T>>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagedListConfig: PagedList.Config,
    private val networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>,
    private val initData: ServiceResponse?
) : PageKeyedDataSource<Int, T>() {

  private var isLoadingInitialData = false
  private var retry: (() -> Any)? = null
  val networkState = MutableLiveData<NetworkState>()
  val initialLoad = MutableLiveData<NetworkState>()

  fun retryAllFailed() {
    val prevRetry = retry
    retry = null
    prevRetry?.invoke()
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
    ioServiceExecutor.execute {
      synchronized(this) {
        if (networkDataSourceAdapter.canFetch(page = params.key, pageSize = params.requestedLoadSize)
            && !isLoadingInitialData) {
          val nextPage = params.key + 1 + params.requestedLoadSize / pagedListConfig.pageSize
          var isLastPage = !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = params.requestedLoadSize)
          val isFirstPage = params.key == firstPage
          networkState.postValue(NetworkState.Loading(params.key, params.requestedLoadSize, isFirstPage, isLastPage))
          networkDataSourceAdapter.pageFetcher.fetchPage(page = params.key, pageSize = params.requestedLoadSize,
              networkResultListener = object : NetworkResultListener<ServiceResponse> {
                override fun onSuccess(response: ServiceResponse) {
                  retry = null
                  callback.onResult(response.getElements(), params.key + 1)
                  isLastPage = !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = params.requestedLoadSize)
                  networkState.postValue(
                      NetworkState.Loaded(params.key, params.requestedLoadSize, isFirstPage, isLastPage)
                  )
                }

                override fun onError(t: Throwable) {
                  retry = {
                    loadAfter(params, callback)
                  }
                  networkState.postValue(
                      NetworkState.Error(t, params.key, params.requestedLoadSize, isFirstPage, isLastPage)
                  )
                }
              })
        }
      }
    }
  }

  fun onInitialDataLoaded() {
    synchronized(this) {
      isLoadingInitialData = false
    }
  }

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
    if (initData == null) {
      ioServiceExecutor.execute {
        synchronized(this) {
          isLoadingInitialData = true
          var nextPage = firstPage + 1 + params.requestedLoadSize / pagedListConfig.pageSize
          var isLastPage = !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = params.requestedLoadSize)
          val loadingState = NetworkState.Loading(firstPage, params.requestedLoadSize, true, isLastPage)
          networkState.postValue(loadingState)
          initialLoad.postValue(loadingState)
          networkDataSourceAdapter.pageFetcher.fetchPage(page = firstPage, pageSize = params.requestedLoadSize,
              networkResultListener = object : NetworkResultListener<ServiceResponse> {
                override fun onSuccess(response: ServiceResponse) {
                  retry = null
                  nextPage = firstPage + params.requestedLoadSize / pagedListConfig.pageSize
                  isLastPage = !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = params.requestedLoadSize)
                  val success = NetworkState.Loaded(firstPage, params.requestedLoadSize, true, isLastPage)
                  networkState.postValue(success)
                  initialLoad.postValue(success)
                  onInitialDataLoaded()
                  callback.onResult(response.getElements(), -1, nextPage)
                }

                override fun onError(t: Throwable) {
                  onInitialDataLoaded()
                  retry = {
                    loadInitial(params, callback)
                  }
                  val error = NetworkState.Error(t, firstPage, params.requestedLoadSize, true, false)
                  networkState.postValue(error)
                  initialLoad.postValue(error)
                }
              })
        }
      }
    } else {
      retry = null
      val nextPage = firstPage + params.requestedLoadSize / pagedListConfig.pageSize
      callback.onResult(initData.getElements(), -1, nextPage)
      val isLastPage = !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = params.requestedLoadSize)

      val success = NetworkState.Loaded(firstPage, params.requestedLoadSize, true, isLastPage)
      networkState.postValue(success)
      initialLoad.postValue(success)
    }
  }

  @AnyThread
  fun resetData(resetDataCollection: MutableLiveData<ServiceResponse>): LiveData<NetworkState> {
    val resetNetworkState = MutableLiveData<NetworkState>()
    ioServiceExecutor.execute {
      synchronized(this) {
        if (!isLoadingInitialData) {
          isLoadingInitialData = true
          resetNetworkState.postValue(NetworkState.Loading(firstPage, pagedListConfig.initialLoadSizeHint, true, false))
          networkDataSourceAdapter.pageFetcher.fetchPage(page = firstPage,
              pageSize = pagedListConfig.initialLoadSizeHint,
              networkResultListener = object : NetworkResultListener<ServiceResponse> {
                override fun onSuccess(response: ServiceResponse) {
                  onInitialDataLoaded()
                  resetNetworkState.postValue(
                      NetworkState.Loaded(firstPage, pagedListConfig.initialLoadSizeHint, true, false)
                  )
                  resetDataCollection.postValue(response)
                  invalidate()
                }

                override fun onError(t: Throwable) {
                  onInitialDataLoaded()
                  resetNetworkState.postValue(
                      NetworkState.Error(t, firstPage, pagedListConfig.initialLoadSizeHint, true, false)
                  )
                }
              })
        } else {
          val exception = IllegalStateException("The first page cannot be fetched")
          resetNetworkState.postValue(
              NetworkState.Error(exception, firstPage, pagedListConfig.initialLoadSizeHint, true, false)
          )
        }
      }
    }
    return resetNetworkState
  }
}
