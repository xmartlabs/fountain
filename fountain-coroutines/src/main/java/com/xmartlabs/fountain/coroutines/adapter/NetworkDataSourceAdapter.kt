package com.xmartlabs.fountain.coroutines.adapter

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import kotlinx.coroutines.experimental.Deferred

/**
 * It's used to fetch each page from the service.
 * It's based on [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html).
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

/**
 * It's used to fetch the service data where the service response is a not paged list.
 * It's based on [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html).
 */
interface NotPagedCoroutinePageFetcher<T : ListResponse<*>> {
  fun fetchData(): Deferred<T>
}

/**It's a [NetworkDataSourceAdapter] based on a [CoroutinePageFetcher]. */
interface CoroutineNetworkDataSourceAdapter<T : ListResponse<*>> : NetworkDataSourceAdapter<CoroutinePageFetcher<T>>
