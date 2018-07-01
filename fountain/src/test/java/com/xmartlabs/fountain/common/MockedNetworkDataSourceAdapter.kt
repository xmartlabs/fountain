package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.PageFetcher
import io.reactivex.Single
import io.reactivex.SingleEmitter

class MockedNetworkDataSourcePageFetcher<T> : PageFetcher<T>{
  var emitter: SingleEmitter<T>? = null

  override fun fetchPage(page: Int, pageSize: Int): Single<out T> {
    return Single.create { emitter ->
      this.emitter = emitter
    }
  }
}

class MockedNetworkDataSourceAdapter<T> : NetworkDataSourceAdapter<T> {
  val pageFetcher = MockedNetworkDataSourcePageFetcher<T>()
  val emitter : SingleEmitter<T>?
      get() = pageFetcher.emitter

  override fun fetchPage(page: Int, pageSize: Int): Single<out T> =
    pageFetcher.fetchPage(page, pageSize)

  override fun canFetch(page: Int, pageSize: Int): Boolean = true
}
