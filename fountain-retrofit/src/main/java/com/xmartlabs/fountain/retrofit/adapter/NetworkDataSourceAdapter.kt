package com.xmartlabs.fountain.retrofit.adapter

import android.support.annotation.CheckResult
import retrofit2.Call

/**
 * It is used to fetch each page from the service.
 * It's based on [Retrofit](https://square.github.io/retrofit/) service response.
 */
interface RetrofitPageFetcher<T> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Call] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Call<T>
}

/** It is used to handle the paging state */
interface RetrofitNetworkDataSourceAdapter<T> {
  val retrofitPageFetcher: RetrofitPageFetcher<T>

  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}
