package com.xmartlabs.fountain.retrofit.adapter

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import retrofit2.Call

/**
 * It's used to fetch each page from the service.
 * It's based on [Retrofit](https://square.github.io/retrofit/) service response.
 */
interface RetrofitPageFetcher<T : ListResponse<*>> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Call] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Call<T>
}

/**
 * It's used to fetch the service data where the service response is a not paged list.
 * It's based on [Retrofit](https://square.github.io/retrofit/) service response.
 */
interface NotPagedRetrifitPageFetcher<T : ListResponse<*>> {
  fun fetchData(): Call<T>
}

/**It's a [NetworkDataSourceAdapter] based on a [RetrofitPageFetcher]. */
interface RetrofitNetworkDataSourceAdapter<T : ListResponse<*>> : NetworkDataSourceAdapter<RetrofitPageFetcher<T>>
