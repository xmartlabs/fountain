package com.xmartlabs.xlpagingbypagenumber.feature.network

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.arch.paging.PagedList
import com.xmartlabs.xlpagingbypagenumber.ListResponse
import com.xmartlabs.xlpagingbypagenumber.NetworkState
import com.xmartlabs.xlpagingbypagenumber.fetcher.ListResponsePagingHandler
import com.xmartlabs.xlpagingbypagenumber.common.subscribeOn
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor

internal class NetworkPagedDataSource<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagedListConfig: PagedList.Config,
    private val pagingHandler: ListResponsePagingHandler<T>
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
    synchronized(this) {
      if (pagingHandler.canFetch(page = params.key, pageSize = params.requestedLoadSize) && !isLoadingInitialData) {
        networkState.postValue(NetworkState.LOADING)
        pagingHandler.fetchPage(page = params.key, pageSize = params.requestedLoadSize)
            .subscribeOn(ioServiceExecutor)
            .subscribe(object : SingleObserver<ListResponse<T>> {
              override fun onSuccess(data: ListResponse<T>) {
                retry = null
                callback.onResult(data.getElements(), params.key + 1)
                networkState.postValue(NetworkState.LOADED)
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(t: Throwable) {
                retry = {
                  loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(t))
              }
            })
      }
    }
  }

  fun onInitialDataLoaded() {
    synchronized(this) {
      isLoadingInitialData = false
    }
  }

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
    synchronized(this) {
        isLoadingInitialData = true
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
      pagingHandler.fetchPage(page = firstPage, pageSize = params.requestedLoadSize)
            .subscribeOn(ioServiceExecutor)
            .subscribe(object : SingleObserver<ListResponse<T>> {
              override fun onSuccess(data: ListResponse<T>) {
                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                onInitialDataLoaded()
                val nextPage = firstPage + params.requestedLoadSize / pagedListConfig.pageSize
                callback.onResult(data.getElements(), -1, nextPage)
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(t: Throwable) {
                onInitialDataLoaded()
                retry = {
                  loadInitial(params, callback)
                }
                val error = NetworkState.error(t)
                networkState.postValue(error)
                initialLoad.postValue(error)
              }
            })
      }
  }
}
