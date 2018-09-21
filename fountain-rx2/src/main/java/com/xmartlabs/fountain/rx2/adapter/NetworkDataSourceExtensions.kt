package com.xmartlabs.fountain.rx2.adapter

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

internal fun <T> RxPageFetcher<T>.toPageFetcher(): PageFetcher<T> {
  return object : PageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      try {
        networkResultListener.onSuccess(this@toPageFetcher.fetchPage(page = page, pageSize = pageSize).blockingGet())
      } catch (throwable: Throwable){
        networkResultListener.onError(throwable)
      }
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
