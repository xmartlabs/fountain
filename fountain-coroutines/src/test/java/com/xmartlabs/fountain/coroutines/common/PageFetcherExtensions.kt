package com.xmartlabs.fountain.coroutines.common

import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import com.xmartlabs.fountain.coroutines.adapter.CoroutinePageFetcher
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred

fun <T> PageFetcher<T>.toCoroutinePageFetcher(): CoroutinePageFetcher<T> {
  return object : CoroutinePageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int): Deferred<T> {
      val deferred = CompletableDeferred<T>()
      fetchPage(page, pageSize, object : NetworkResultListener<T> {
        override fun onError(t: Throwable) {
          deferred.completeExceptionally(t)
        }

        override fun onSuccess(response: T) {
          deferred.complete(response)
        }
      })
      return deferred
    }
  }
}
