package com.xmartlabs.fountain.rx2.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.notifyFromCallable

private fun <T : ListResponse<*>> RxPageFetcher<T>.toBasePageFetcher() = object : BasePageFetcher<T> {
  @WorkerThread
  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    networkResultListener.notifyFromCallable {
      fetchPage(page = page, pageSize = pageSize).blockingGet()
    }
  }
}

internal fun <T : ListResponse<*>> RxNetworkDataSourceAdapter<T>.toBaseNetworkDataSourceAdapter() =
    object : BaseNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toBaseNetworkDataSourceAdapter.pageFetcher.toBasePageFetcher()

      override fun canFetch(page: Int, pageSize: Int): Boolean =
          this@toBaseNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
    }

private fun <T : ListResponse<*>> NotPagedRxPageFetcher<T>.toBasePageFetcher() = object : BasePageFetcher<T> {
  @WorkerThread
  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    networkResultListener.notifyFromCallable { this@toBasePageFetcher.fetchData().blockingGet() }
  }
}

internal fun <T : ListResponse<*>> NotPagedRxPageFetcher<T>.toBaseNetworkDataSourceAdapter() =
    object : BaseNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toBaseNetworkDataSourceAdapter.toBasePageFetcher()

      override fun canFetch(page: Int, pageSize: Int): Boolean =
          FountainConstants.DEFAULT_FIRST_PAGE == page
    }
