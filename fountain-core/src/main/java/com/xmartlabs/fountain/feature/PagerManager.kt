package com.xmartlabs.fountain.feature

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import java.util.concurrent.Executor

internal class PagerManager<NetworkValue, NetworkResponse : ListResponse<out NetworkValue>>(
    private val networkDataSourceAdapter: BaseNetworkDataSourceAdapter<NetworkResponse>,
    private val pagedListConfig: PagedList.Config,
    private val ioServiceExecutor: Executor,
    private val firstPage: Int
) {
  private var isLoadingInitialData = false
  internal var page = firstPage
  var helper = PagingRequestHelper(ioServiceExecutor)
  val networkState = MutableLiveData<NetworkState>()

  internal fun onInitialDataPreloaded() {
    page = 1 + firstPage + pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
    val isLastPage = !networkDataSourceAdapter.canFetch(page = page + 1, pageSize = pagedListConfig.pageSize)
    val success = NetworkState.Loaded(firstPage, pagedListConfig.initialLoadSizeHint, true, isLastPage)
    networkState.postValue(success)
  }

  fun loadInitialData(processResponse: (request: Request, response: NetworkResponse, callback: Callback) -> Unit) {
    synchronized(this) {
      isLoadingInitialData = true
      helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
        val callback = Callback.fromCallback(it)
        val request = Request(firstPage, true, pagedListConfig)
        performRequest(processResponse, networkState, callback, request)
      }
    }
  }

  fun loadNextPage(processResponse: (request: Request, response: NetworkResponse, callback: Callback) -> Unit) {
    val request = Request(page, false, pagedListConfig)
    if (networkDataSourceAdapter.canFetch(page = request.page, pageSize = pagedListConfig.pageSize)) {
      synchronized(this) {
        if (!isLoadingInitialData) {
          helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            val callback = Callback.fromCallback(it)
            performRequest(processResponse, networkState, callback, request)
          }
        }
      }
    }
  }

  fun resetData(processResponse: (request: Request, response: NetworkResponse, callback: Callback) -> Unit)
      : LiveData<NetworkState> {
    val resetNetworkState = MutableLiveData<NetworkState>()
    val callback = object : Callback() {
      override fun onSuccess() {
        helper = PagingRequestHelper(ioServiceExecutor)
      }

      override fun onError(throwable: Throwable) {}
    }

    synchronized(this) {
      if (!isLoadingInitialData) {
        isLoadingInitialData = true

        val request = Request(firstPage, true, pagedListConfig)
        performRequest(processResponse, resetNetworkState, callback, request)
      } else {
        resetNetworkState.postValue(
            NetworkState.Error(
                IllegalStateException("The first page cannot be fetched"),
                firstPage,
                pagedListConfig.initialLoadSizeHint,
                true,
                isLastPage(page + 1)
            )
        )
      }
    }
    return resetNetworkState
  }

  private fun createLoadingState(page: Int, nextPage: Int, pageSize: Int): NetworkState.Loading =
      NetworkState.Loading(page, pageSize, page == firstPage, isLastPage(nextPage))

  private fun onInitialDataLoaded() {
    synchronized(this) {
      isLoadingInitialData = false
    }
  }

  private fun performRequest(
      processResponse: (request: Request, response: NetworkResponse, callback: Callback) -> Unit,
      networkState: MutableLiveData<NetworkState>,
      callback: Callback,
      request: Request
  ) {
    ioServiceExecutor.execute {
      val loadingState = if (request.isFirstPage) {
        val nextPage = firstPage + 1 + pagedListConfig.initialLoadSizeHint / pagedListConfig.pageSize
        createLoadingState(firstPage, nextPage, pagedListConfig.initialLoadSizeHint)
      } else {
        createLoadingState(request.page, request.page + 1, pagedListConfig.pageSize)
      }
      networkState.postValue(loadingState)

      networkDataSourceAdapter.pageFetcher.fetchPage(request.page, request.pageSize,
          object : NetworkResultListener<NetworkResponse> {
            override fun onSuccess(response: NetworkResponse) =
                saveInDatabaseIfNeeded(processResponse, networkState, request, response, callback)

            override fun onError(t: Throwable) =
                onErrorHappened(networkState, request, t, callback)
          })
    }
  }

  private fun onErrorHappened(
      networkState: MutableLiveData<NetworkState>,
      request: Request,
      throwable: Throwable,
      callback: Callback
  ) {
    if (request.isFirstPage) {
      onInitialDataLoaded()
    }
    val isLastPage = isLastPage(request.requestedPageCount + request.page + 1)
    networkState.postValue(
        NetworkState.Error(throwable, request.page, request.pageSize, request.isFirstPage, isLastPage)
    )
    callback.onError(throwable)
  }

  private fun saveInDatabaseIfNeeded(
      processResponse: (request: Request, response: NetworkResponse, callback: Callback) -> Unit,
      networkState: MutableLiveData<NetworkState>,
      request: Request,
      response: NetworkResponse,
      callback: Callback) {
    processResponse.invoke(request, response, object : Callback() {
      override fun onSuccess() {
        val isLastPage = isLastPage(request.page + request.pageSize + 1)
        networkState.postValue(NetworkState.Loaded(request.page, request.pageSize, request.isFirstPage, isLastPage))
        page = request.page + request.requestedPageCount
        if (request.isFirstPage) {
          onInitialDataLoaded()
        }
        callback.onSuccess()
      }

      override fun onError(throwable: Throwable) {
        onErrorHappened(networkState, request, throwable, callback)
      }
    })
  }

  private fun isLastPage(nextPage: Int) =
      !networkDataSourceAdapter.canFetch(page = nextPage, pageSize = pagedListConfig.pageSize)

  internal data class Request(val page: Int, val pageSize: Int, val requestedPageCount: Int, val isFirstPage: Boolean) {
    constructor(page: Int, isFirstPage: Boolean, config: PagedList.Config) : this(
        page = page,
        pageSize = if (isFirstPage) config.initialLoadSizeHint else config.pageSize,
        requestedPageCount = if (isFirstPage) config.initialLoadSizeHint / config.pageSize else 1,
        isFirstPage = isFirstPage
    )
  }

  internal abstract class Callback {
    companion object {
      fun fromCallback(callback: PagingRequestHelper.Request.Callback)
          : Callback = object : Callback() {
        override fun onSuccess() = callback.recordSuccess()

        override fun onError(throwable: Throwable) = callback.recordFailure(throwable)
      }
    }

    abstract fun onSuccess()
    abstract fun onError(throwable: Throwable)
  }
}
