package com.xmartlabs.fountain.testutils

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher

class MockedNetworkDataSourcePageFetcher<T> : PageFetcher<T> {
  private var fetchPageListener: NetworkResultListener<T>? = null
  var networkResultListener: NetworkResultListener<T> = object  : NetworkResultListener<T>{
    override fun onSuccess(response: T) {
      fetchPageListener?.onSuccess(response)
      fetchPageListener = null
    }

    override fun onError(t: Throwable) {
      fetchPageListener?.onError(t)
      fetchPageListener = null
    }
  }

  val wasPageRequired
    get() = fetchPageListener != null

  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    this.fetchPageListener = networkResultListener
  }
}

class MockedNetworkDataSourceAdapter<T> : NetworkDataSourceAdapter<T> {
  override val pageFetcher = MockedNetworkDataSourcePageFetcher<T>()
  val networkResultListener: NetworkResultListener<T>?
    get() = pageFetcher.networkResultListener

  override fun canFetch(page: Int, pageSize: Int): Boolean = true
}
