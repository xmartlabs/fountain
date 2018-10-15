package com.xmartlabs.fountain.coroutines.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.common.FountainConstants
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
    object : BaseNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toBaseNetworkDataSourceAdapter.pageFetcher.toBasePageFetcher()

      override fun canFetch(page: Int, pageSize: Int): Boolean =
          this@toBaseNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
    }

private fun <T : ListResponse<*>> NotPagedCoroutinePageFetcher<T>.toBasePageFetcher() = object : BasePageFetcher<T> {
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
    object : BaseNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toBaseNetworkDataSourceAdapter.toBasePageFetcher()

      override fun canFetch(page: Int, pageSize: Int): Boolean =
          FountainConstants.DEFAULT_FIRST_PAGE == page
    }
