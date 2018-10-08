package com.xmartlabs.fountain.coroutines.adapter

import android.support.annotation.CheckResult
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import kotlinx.coroutines.experimental.Deferred

/**
 * It is used to fetch each page from the service.
 * It's based on [RxJava](https://github.com/ReactiveX/RxJava).
 */
interface CoroutinePageFetcher<T : ListResponse<*>> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Deferred] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Deferred<T>
}

interface CoroutineNetworkDataSourceAdapter<T : ListResponse<*>> : NetworkDataSourceAdapter<CoroutinePageFetcher<T>>
