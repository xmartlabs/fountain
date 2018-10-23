package com.xmartlabs.fountain.coroutines.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.common.BaseNetworkDataSourceAdapterFactory
import com.xmartlabs.fountain.common.notifyFromCallable
import kotlinx.coroutines.experimental.runBlocking

private fun <T : ListResponse<*>> CoroutinePageFetcher<T>.toBasePageFetcher(): BasePageFetcher<T> {
  return object : BasePageFetcher<T> {
    @WorkerThread
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) =
        networkResultListener.notifyFromCallable {
          runBlocking {
            this@toBasePageFetcher.fetchPage(page = page, pageSize = pageSize).await()
          }
        }
  }
}

internal fun <T : ListResponse<*>> CoroutineNetworkDataSourceAdapter<T>.toBaseNetworkDataSourceAdapter() =
    BaseNetworkDataSourceAdapterFactory.createFromAdapter(pageFetcher.toBasePageFetcher(), this)

private fun <T : ListResponse<*>> NotPagedCoroutinePageFetcher<T>.toBasePageFetcher(): BasePageFetcher<T> =
    object : BasePageFetcher<T> {
      @WorkerThread
      override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
        networkResultListener.notifyFromCallable {
          runBlocking {
            this@toBasePageFetcher.fetchData().await()
          }
        }
      }
    }

internal fun <T : ListResponse<*>> NotPagedCoroutinePageFetcher<T>.toBaseNetworkDataSourceAdapter() =
    BaseNetworkDataSourceAdapterFactory.createFromNotPagedPageFetcher(toBasePageFetcher())
