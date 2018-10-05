package com.xmartlabs.fountain.coroutines.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import kotlinx.coroutines.experimental.runBlocking

internal fun <T : ListResponse<*>> CoroutinePageFetcher<T>.toPageFetcher(): PageFetcher<T> {
  return object : PageFetcher<T> {
    @WorkerThread
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      runBlocking {
        try {
          networkResultListener.onSuccess(this@toPageFetcher.fetchPage(page = page, pageSize = pageSize).await())
        } catch (throwable: Throwable) {
          networkResultListener.onError(throwable)
        }
      }
    }
  }
}

internal fun <T : ListResponse<*>> CoroutineNetworkDataSourceAdapter<T>.toNetworkDataSourceAdapter(): NetworkDataSourceAdapter<T> {
  return object : NetworkDataSourceAdapter<T> {
    override val pageFetcher = coroutinePageFetcher.toPageFetcher()

    override fun canFetch(page: Int, pageSize: Int): Boolean =
        this@toNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
  }
}
