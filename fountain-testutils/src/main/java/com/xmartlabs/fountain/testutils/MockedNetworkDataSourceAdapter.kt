package com.xmartlabs.fountain.testutils

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher


class MockedNetworkDataSourcePageFetcher<T> : PageFetcher<T> {
  private var networkResultListener: NetworkResultListener<T>? = null
  private var pendingResponse: T? = null
  private var pendingError: Throwable? = null

  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    synchronized(this) {
      pendingResponse
          ?.let {
            networkResultListener.onSuccess(it)
            pendingResponse = null
          }
          .orDo {
            pendingError
                ?.let {
                  networkResultListener.onError(it)
                  pendingError = null
                }
                .orDo { this.networkResultListener = networkResultListener }
          }
    }
  }

  fun onSuccess(response: T) {
    synchronized(this) {
      networkResultListener
          ?.let {
            it.onSuccess(response)
            networkResultListener = null
          }
          .orDo {
            pendingError = null
            pendingResponse = response
          }
    }
  }

  fun onError(t: Throwable) {
    synchronized(this) {
      networkResultListener
          ?.let {
            it.onError(t)
            networkResultListener = null
          }
          .orDo {
            pendingError = t
            pendingResponse = null
          }
    }
  }
}

class MockedNetworkDataSourceAdapter<T> : NetworkDataSourceAdapter<T> {
  override val pageFetcher = MockedNetworkDataSourcePageFetcher<T>()

  override fun canFetch(page: Int, pageSize: Int): Boolean = true
}

private fun <T> T?.orDo(action: () -> T) = this ?: action()
