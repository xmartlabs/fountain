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

internal class NetworkPagedDataSource<T, ServiceResponse : ListResponse<T>>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagedListConfig: PagedList.Config,
    private val networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>,
    private var initData: ServiceResponse?
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
      if (networkDataSourceAdapter.canFetch(page = params.key, pageSize = params.requestedLoadSize)
          && !isLoadingInitialData) {
        networkState.postValue(NetworkState.Loading(params.key))
        networkDataSourceAdapter.fetchPage(page = params.key, pageSize = params.requestedLoadSize)
            .subscribeOn(ioServiceExecutor)
            .subscribe(object : SingleObserver<ServiceResponse> {
              override fun onSuccess(data: ServiceResponse) {
                retry = null
                callback.onResult(data.getElements(), params.key + 1)
                networkState.postValue(NetworkState.Success)
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(t: Throwable) {
                retry = {
                  loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.Error(t))
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
        networkState.postValue(NetworkState.Loading(firstPage))
        initialLoad.postValue(NetworkState.Loading(firstPage))
        networkDataSourceAdapter.fetchPage(page = firstPage, pageSize = params.requestedLoadSize)
            .subscribeOn(ioServiceExecutor)
            .subscribe(object : SingleObserver<ServiceResponse> {
              override fun onSuccess(data: ServiceResponse) {
                retry = null
                networkState.postValue(NetworkState.Success)
                initialLoad.postValue(NetworkState.Success)
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
                val error = NetworkState.Error(t)
                networkState.postValue(error)
                initialLoad.postValue(error)
              }
            })
      }
    } else {
      retry = null
      val nextPage = firstPage + params.requestedLoadSize / pagedListConfig.pageSize
      callback.onResult(initData!!.getElements(), -1, nextPage)
      networkState.postValue(NetworkState.Success)
      initialLoad.postValue(NetworkState.Success)
    }
  }

  @AnyThread
  fun resetData(resetDataCollection: MutableLiveData<ServiceResponse>): LiveData<NetworkState> {
    val resetNetworkState = MutableLiveData<NetworkState>()
    synchronized(this) {
      if (!isLoadingInitialData) {
        isLoadingInitialData = true
        resetNetworkState.postValue(NetworkState.Loading(firstPage))
        networkDataSourceAdapter.fetchPage(page = firstPage, pageSize = pagedListConfig.initialLoadSizeHint)
            .subscribeOn(ioServiceExecutor)
            .subscribe(object : SingleObserver<ServiceResponse> {
              override fun onSuccess(data: ServiceResponse) {
                onInitialDataLoaded()
                resetNetworkState.postValue(NetworkState.Success)
                resetDataCollection.postValue(data)
                invalidate()
              }

              override fun onSubscribe(d: Disposable) {}

              override fun onError(t: Throwable) {
                onInitialDataLoaded()
                resetNetworkState.postValue(NetworkState.Error(t))
              }
            })
      } else {
        resetNetworkState.postValue(NetworkState.Error(IllegalStateException("The first page cannot be fetched")))
      }
    }
    return resetNetworkState
  }
}
