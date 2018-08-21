package com.xmartlabs.fountain.rx2.common

import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import com.xmartlabs.fountain.rx2.adapter.RxPageFetcher
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

fun <T> PageFetcher<T>.toRxPageFetcher()
    : RxPageFetcher<T> {
  return object : RxPageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int): Single<T> {
      return Single.create {
        this@toRxPageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<T> {
          override fun onSuccess(response: T) = it.onSuccess(response)

          override fun onError(t: Throwable) = it.onError(t)
        })
      }
    }
  }
}
