package com.xmartlabs.fountain.retrofit.adapter

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.KnownSizeResponseManager
import retrofit2.Call

/** A [RetrofitNetworkDataSourceAdapter] factory */
object RetrofitNetworkDataSourceAdapterFactory {
  /**
   * Provides a [RetrofitNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the entity count in the response.
   *
   * @param Value The value that the service returns.
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <ListResponseValue : ListResponseWithEntityCount<*>> fromTotalEntityCountListResponse(
      pageFetcher: RetrofitPageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RetrofitNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val pageFetcher: RetrofitPageFetcher<ListResponseValue>
      get() = object : RetrofitPageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Call<ListResponseValue> =
            pageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalEntityResponseArrived(it) }
      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }

  /**
   * Provides a [RetrofitNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the page count in the response.
   *
   * @param Value The value that the service returns.
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <ListResponseValue : ListResponseWithPageCount<*>> fromTotalPageCountListResponse(
      pageFetcher: RetrofitPageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RetrofitNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val pageFetcher: RetrofitPageFetcher<ListResponseValue>
      get() = object : RetrofitPageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Call<ListResponseValue> =
            pageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, it) }
      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }
}

/**
 * Provides a [RetrofitNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the entity count in the response.
 *
 * @param ServiceResponse The response type that the service returns.
 * @param firstPage The first page number, defined by the service.
 */
fun <ServiceResponse : ListResponseWithEntityCount<*>>
    RetrofitPageFetcher<ServiceResponse>.toTotalEntityCountNetworkDataSourceAdapter(
    firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
) = RetrofitNetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(this, firstPage)

/**
 * Provides a [RetrofitNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the page count in the response.
 *
 * @param ServiceResponse The response type that the service returns.
 * @param firstPage The first page number, defined by the service.
 */
fun <ServiceResponse : ListResponseWithPageCount<*>>
    RetrofitPageFetcher<ServiceResponse>.toTotalPageCountNetworkDataSourceAdapter(
    firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
) = RetrofitNetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(this, firstPage)
