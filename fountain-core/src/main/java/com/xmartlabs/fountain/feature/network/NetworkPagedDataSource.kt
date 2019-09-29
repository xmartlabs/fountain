package com.xmartlabs.fountain.feature.network

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.feature.PagerManager
import java.util.concurrent.Executor

internal class NetworkPagedDataSource<T, ServiceResponse : ListResponse<out T>>(
  firstPage: Int,
  ioServiceExecutor: Executor,
  pagedListConfig: PagedList.Config,
  networkDataSourceAdapter: BaseNetworkDataSourceAdapter<ServiceResponse>,
  private val initData: ServiceResponse?
) : PageKeyedDataSource<Int, T>() {

  val networkState: LiveData<NetworkState>
    get() = pageManager.networkState
  val retry: (() -> Any)
    get() = pageManager.helper::retryAllFailed

  private val pageManager = PagerManager(
      networkDataSourceAdapter = networkDataSourceAdapter,
      pagedListConfig = pagedListConfig,
      ioServiceExecutor = ioServiceExecutor,
      firstPage = firstPage
  )

  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
    if (initData == null) {
      pageManager.loadInitialData { request, response, responseCallback ->
        callback.onResult(response.getElements(), -1, request.requestedPageCount + 1)
        responseCallback.onSuccess()
      }
    } else {
      pageManager.onInitialDataPreloaded()
      callback.onResult(initData.getElements(), -1, pageManager.page )
    }
  }

  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}

  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
    pageManager.loadNextPage { request, response, responseCallback ->
      callback.onResult(response.getElements(), request.page + 1)
      responseCallback.onSuccess()
    }
  }

  @AnyThread
  fun resetData(resetDataCollection: MutableLiveData<ServiceResponse>): LiveData<NetworkState> {
    return pageManager.resetData { _, response, callback ->
      resetDataCollection.postValue(response)
      invalidate()
      callback.onSuccess()
    }
  }
}
