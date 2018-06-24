package com.xmartlabs.fountain.feature.network

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.arch.paging.PagedList
import android.support.annotation.AnyThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.common.subscribeOn
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor

internal class NetworkPagedDataSource<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagedListConfig: PagedList.Config,
    private val networkDataSourceAdapter: NetworkDataSourceAdapter<out ListResponse<T>>,
    private var initData: List<T>?
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
      if (networkDataSourceAdapter.canFetch(page = params.key, pageSize = params.requestedLoadSize) && !isLoadingInitialData) {
        networkState.postValue(NetworkState.LOADING)
        networkDataSourceAdapter.fetchPage(page = params.key, pageSize = params.requestedLoadSize)
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
    if (initData == null) {
      synchronized(this) {
        isLoadingInitialData = true
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        networkDataSourceAdapter.fetchPage(page = firstPage, pageSize = params.requestedLoadSize)
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
    } else {
      retry = null
      val nextPage = firstPage + params.requestedLoadSize / pagedListConfig.pageSize
      callback.onResult(initData!!, -1, nextPage)
      networkState.postValue(NetworkState.LOADED)
      initialLoad.postValue(NetworkState.LOADED)
    }
  }

  @AnyThread
  fun resetData(resetDataCollection: MutableLiveData<List<T>>): LiveData<NetworkState> {
    val resetNetworkState = MutableLiveData<NetworkState>()
    synchronized(this) {
      if (!isLoadingInitialData) {
        isLoadingInitialData = true
        resetNetworkState.postValue(NetworkState.LOADING)
        networkDataSourceAdapter.fetchPage(page = firstPage, pageSize = pagedListConfig.initialLoadSizeHint)
            .subscribeOn(ioServiceExecutor)
            .subscribe(object : SingleObserver<ListResponse<T>> {
              override fun onSuccess(data: ListResponse<T>) {
                onInitialDataLoaded()
                resetNetworkState.postValue(NetworkState.LOADED)
                resetDataCollection.postValue(data.getElements())
                invalidate()
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(t: Throwable) {
                onInitialDataLoaded()
                resetNetworkState.postValue(NetworkState.error(t))
              }
            })
      } else {
        resetNetworkState.postValue(NetworkState.error(IllegalStateException("The first page cannot be fetched")))
      }
    }
    return resetNetworkState
  }
}
