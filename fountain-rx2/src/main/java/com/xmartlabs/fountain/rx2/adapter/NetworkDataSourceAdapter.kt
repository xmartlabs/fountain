package com.xmartlabs.fountain.rx2.adapter

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import io.reactivex.Single

/**
 * It is used to fetch each page from the service.
 * It's based on [RxJava](https://github.com/ReactiveX/RxJava).
 */
interface RxPageFetcher<T : ListResponse<*>> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Single] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Single<T>
}

/**It's a [NetworkDataSourceAdapter] based on a [RxPageFetcher]. */
interface RxNetworkDataSourceAdapter<T : ListResponse<*>> : NetworkDataSourceAdapter<RxPageFetcher<T>>
