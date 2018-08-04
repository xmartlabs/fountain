package com.xmartlabs.fountain.rx2.adapter

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.KnownSizeResponseManager
import io.reactivex.Single

/** A [RxNetworkDataSourceAdapter] factory */
object NetworkDataSourceAdapterFactory {
  /**
   * Provides a [RxNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the entity count in the response.
   *
   * @param Value The value that the service returns.
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <Value, ListResponseValue : ListResponseWithEntityCount<Value>> fromTotalEntityCountListResponse(
      pageFetcher: RxPageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RxNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val rxPageFetcher: RxPageFetcher<ListResponseValue>
      get() = object : RxPageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseValue> =
            pageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalEntityResponseArrived(it) }

      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }

  /**
   * Provides a [RxNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the page count in the response.
   *
   * @param Value The value that the service returns.
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <Value, ListResponseValue : ListResponseWithPageCount<Value>> fromTotalPageCountListResponse(
      pageFetcher: RxPageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RxNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val rxPageFetcher: RxPageFetcher<ListResponseValue>
      get() = object : RxPageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseValue> =
            pageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, it) }

      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }
}
