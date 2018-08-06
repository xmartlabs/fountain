package com.xmartlabs.fountain.rx2.common

import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.adapter.RxPageFetcher
import io.reactivex.Single
import io.reactivex.SingleEmitter

class MockedNetworkDataSourcePageFetcher<T> : RxPageFetcher<T> {
  var emitter : SingleEmitter<T>? = null
  var networkResultListener: NetworkResultListener<T> = object : NetworkResultListener<T> {
    override fun onSuccess(response: T) {
      emitter?.onSuccess(response)
      emitter = null
    }

    override fun onError(t: Throwable) {
      emitter?.onError(t)
      emitter= null
    }
  }

  val wasPageRequired
    get() = emitter != null

  override fun fetchPage(page: Int, pageSize: Int): Single<T> {
    return Single.create<T> {
      emitter = it
    }
  }
}

class MockedNetworkDataSourceAdapter<T> : RxNetworkDataSourceAdapter<T> {
  override val rxPageFetcher = MockedNetworkDataSourcePageFetcher<T>()

  val networkResultListener: NetworkResultListener<T>?
    get() = rxPageFetcher.networkResultListener

  override fun canFetch(page: Int, pageSize: Int): Boolean = true
}
