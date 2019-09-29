package com.xmartlabs.fountain.adapter

import androidx.annotation.CheckResult
import androidx.annotation.NonNull
import androidx.annotation.WorkerThread


/** It is used to get notify the service response. */
interface NetworkResultListener<T> {
  /** Invoked when the service returned a valid response. */
  fun onSuccess(@NonNull response: T)

  /** Invoked when the service request thrown an error. */
  fun onError(@NonNull t: Throwable)
}

/** It is used to fetch each page from the service. */
interface BasePageFetcher<Response> {
  /**
   *Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [NetworkResultListener] of the type [Response] that represent the service call.
   */
  @WorkerThread
  fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<Response>)
}

interface BaseNetworkDataSourceAdapter<T> : NetworkDataSourceAdapter<BasePageFetcher<T>>

/** It is used to handle the paging state */
interface NetworkDataSourceAdapter<PageFetcher> {
  val pageFetcher: PageFetcher

  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}
