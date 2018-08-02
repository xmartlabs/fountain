package com.xmartlabs.fountain.rx2.adapter

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

private fun <T> NetworkResultListener<T>.toSingleObserver(): SingleObserver<T> = object : SingleObserver<T> {
  override fun onSuccess(t: T) = this@toSingleObserver.onSuccess(t)

  override fun onSubscribe(d: Disposable) {}

  override fun onError(e: Throwable) = this@toSingleObserver.onError(e)
}

internal fun <T> RxPageFetcher<T>.toPageFetcher()
    : PageFetcher<T> {
  return object : PageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      fetchPage(page = page, pageSize = pageSize)
          .subscribe(networkResultListener.toSingleObserver())
    }
  }
}

internal fun <T> RxNetworkDataSourceAdapter<T>.toNetworkDataSourceAdapter()
    : NetworkDataSourceAdapter<T> {
  return object : NetworkDataSourceAdapter<T> {
    override val pageFetcher = rxPageFetcher.toPageFetcher()

    override fun canFetch(page: Int, pageSize: Int): Boolean =
        this@toNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
  }
}



