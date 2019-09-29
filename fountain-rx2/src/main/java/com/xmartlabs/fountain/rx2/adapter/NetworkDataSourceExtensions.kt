package com.xmartlabs.fountain.rx2.adapter

import androidx.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.common.BaseNetworkDataSourceAdapterFactory
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
    BaseNetworkDataSourceAdapterFactory.createFromAdapter(pageFetcher.toBasePageFetcher(), this)

private fun <T : ListResponse<*>> NotPagedRxPageFetcher<T>.toBasePageFetcher() = object : BasePageFetcher<T> {
  @WorkerThread
  override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
    networkResultListener.notifyFromCallable { this@toBasePageFetcher.fetchData().blockingGet() }
  }
}

internal fun <T : ListResponse<*>> NotPagedRxPageFetcher<T>.toBaseNetworkDataSourceAdapter() =
    BaseNetworkDataSourceAdapterFactory.createFromNotPagedPageFetcher(toBasePageFetcher())
