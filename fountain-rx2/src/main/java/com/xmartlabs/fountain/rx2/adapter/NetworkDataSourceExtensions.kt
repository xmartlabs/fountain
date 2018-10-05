package com.xmartlabs.fountain.rx2.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher

internal fun <T : ListResponse<*>> RxPageFetcher<T>.toPageFetcher(): PageFetcher<T> {
  return object : PageFetcher<T> {
    @WorkerThread
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      try {
        networkResultListener.onSuccess(this@toPageFetcher.fetchPage(page = page, pageSize = pageSize).blockingGet())
      } catch (throwable: Throwable) {
        networkResultListener.onError(throwable)
      }
    }
  }
}

internal fun <T : ListResponse<*>> RxNetworkDataSourceAdapter<T>.toNetworkDataSourceAdapter()
    : NetworkDataSourceAdapter<T> {
  return object : NetworkDataSourceAdapter<T> {
    override val pageFetcher = rxPageFetcher.toPageFetcher()

    override fun canFetch(page: Int, pageSize: Int): Boolean =
        this@toNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
  }
}
