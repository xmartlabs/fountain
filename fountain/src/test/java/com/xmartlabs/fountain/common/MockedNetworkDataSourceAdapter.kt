package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher

class MockedNetworkDataSourcePageFetcher<T> : PageFetcher<T> {
  var networkResultListener: NetworkResultListener<T>? = null

  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    this.networkResultListener = networkResultListener
  }
}

class MockedNetworkDataSourceAdapter<T> : NetworkDataSourceAdapter<T> {
  override val pageFetcher = MockedNetworkDataSourcePageFetcher<T>()
  val networkResultListener: NetworkResultListener<T>?
    get() = pageFetcher.networkResultListener

  override fun canFetch(page: Int, pageSize: Int): Boolean = true
}
