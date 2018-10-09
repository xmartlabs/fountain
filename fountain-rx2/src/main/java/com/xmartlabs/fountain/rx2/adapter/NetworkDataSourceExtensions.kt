package com.xmartlabs.fountain.rx2.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener

internal fun <T : ListResponse<*>> RxPageFetcher<T>.toBasePageFetcher() = object : BasePageFetcher<T> {
  @WorkerThread
  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    try {
      networkResultListener.onSuccess(this@toBasePageFetcher.fetchPage(page = page, pageSize = pageSize).blockingGet())
    } catch (throwable: Throwable) {
      networkResultListener.onError(throwable)
    }
  }
}

internal fun <T : ListResponse<*>> RxNetworkDataSourceAdapter<T>.toBaseNetworkDataSourceAdapter() =
    object : BaseNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toBaseNetworkDataSourceAdapter.pageFetcher.toBasePageFetcher()

      override fun canFetch(page: Int, pageSize: Int): Boolean =
          this@toBaseNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
    }
