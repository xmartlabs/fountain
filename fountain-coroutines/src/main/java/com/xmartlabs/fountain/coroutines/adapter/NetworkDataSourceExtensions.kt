package com.xmartlabs.fountain.coroutines.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener
import kotlinx.coroutines.experimental.runBlocking

internal fun <T : ListResponse<*>> CoroutinePageFetcher<T>.toBasePageFetcher(): BasePageFetcher<T> {
  return object : BasePageFetcher<T> {
    @WorkerThread
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      runBlocking {
        @Suppress("TooGenericExceptionCaught")
        try {
          networkResultListener.onSuccess(this@toBasePageFetcher.fetchPage(page = page, pageSize = pageSize).await())
        } catch (throwable: Throwable) {
          networkResultListener.onError(throwable)
        }
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
