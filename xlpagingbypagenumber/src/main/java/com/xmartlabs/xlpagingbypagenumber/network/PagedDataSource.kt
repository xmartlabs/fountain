package com.xmartlabs.xlpagingbypagenumber.network

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.xmartlabs.xlpagingbypagenumber.ListResponse
import com.xmartlabs.xlpagingbypagenumber.NetworkState
import com.xmartlabs.xlpagingbypagenumber.common.ListResponsePageFetcher
import com.xmartlabs.xlpagingbypagenumber.common.subscribeOn
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor

internal class PagedDataSource<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pageFetcher: ListResponsePageFetcher<T>
) : PageKeyedDataSource<Int, T>() {

  private var retry: (() -> Any)? = null
  val networkState = MutableLiveData<NetworkState>()
  val initialLoad = MutableLiveData<NetworkState>()

  fun retryAllFailed() {
    val prevRetry = retry
    retry = null
    prevRetry?.invoke()
  }

  override fun loadBefore(
      params: LoadParams<Int>,
      callback: LoadCallback<Int, T>) {
  }

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
    if (pageFetcher.canFetch(params.key)) {
      networkState.postValue(NetworkState.LOADING)
      pageFetcher.getPage(page = params.key, pageSize = params.requestedLoadSize)
          .subscribeOn(ioServiceExecutor)
          .subscribe(object : SingleObserver<ListResponse<T>> {
            override fun onSuccess(data: ListResponse<T>) {
              retry = null
              callback.onResult(data.getElements(), params.key + 1)
              networkState.postValue(NetworkState.LOADED)
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(t: Throwable) {
              retry = {
                loadAfter(params, callback)
              }
              networkState.postValue(NetworkState.error(t))
            }
          })
    }
  }

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
    if (pageFetcher.canFetch(firstPage)) {
      networkState.postValue(NetworkState.LOADING)
      initialLoad.postValue(NetworkState.LOADING)
      pageFetcher.getPage(page = firstPage, pageSize = params.requestedLoadSize)
          .subscribeOn(ioServiceExecutor)
          .subscribe(object : SingleObserver<ListResponse<T>> {
            override fun onSuccess(data: ListResponse<T>) {
              retry = null
              networkState.postValue(NetworkState.LOADED)
              initialLoad.postValue(NetworkState.LOADED)
              callback.onResult(data.getElements(), -1, 2)
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(t: Throwable) {
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
