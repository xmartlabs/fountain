package com.xmartlabs.fountain.rx2.adapter

import android.support.annotation.CheckResult
import io.reactivex.Single

/**
 * It is used to fetch each page from the service.
 * It's based on [RxJava](https://github.com/ReactiveX/RxJava).
 */
interface RxPageFetcher<T> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Single] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Single<T>
}

/** It is used to handle the paging state */
interface RxNetworkDataSourceAdapter<T> {
  val rxPageFetcher: RxPageFetcher<T>

  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}
