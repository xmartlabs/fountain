package com.xmartlabs.fountain.coroutines.adapter

import android.support.annotation.CheckResult
import kotlinx.coroutines.experimental.Deferred

/**
 * It is used to fetch each page from the service.
 * It's based on [RxJava](https://github.com/ReactiveX/RxJava).
 */
interface CoroutinePageFetcher<T> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Deferred] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Deferred<T>
}

/** It is used to handle the paging state */
interface CoroutineNetworkDataSourceAdapter<T> {
  val coroutinePageFetcher: CoroutinePageFetcher<T>

  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}
