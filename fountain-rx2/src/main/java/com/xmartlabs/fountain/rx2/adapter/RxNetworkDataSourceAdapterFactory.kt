package com.xmartlabs.fountain.rx2.adapter

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.KnownSizeResponseManager
import io.reactivex.Single

/** A [RxNetworkDataSourceAdapter] factory */
object RxNetworkDataSourceAdapterFactory {
  /**
   * Provides a [RxNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the entity count in the response.
   *
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <ListResponseValue : ListResponseWithEntityCount<*>> fromTotalEntityCountListResponse(
      pageFetcher: RxPageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RxNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val pageFetcher: RxPageFetcher<ListResponseValue>
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
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <ListResponseValue : ListResponseWithPageCount<*>> fromTotalPageCountListResponse(
      pageFetcher: RxPageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RxNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val pageFetcher: RxPageFetcher<ListResponseValue>
      get() = object : RxPageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseValue> =
            pageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, it) }
      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }
}

/**
 * Provides a [RxNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the entity count in the response.
 *
 * @param ServiceResponse The response type returned by the service.
 * @param firstPage The first page number, defined by the service.
 */
fun <ServiceResponse : ListResponseWithEntityCount<*>>
    RxPageFetcher<ServiceResponse>.toTotalEntityCountNetworkDataSourceAdapter(
        firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
    ) = RxNetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(this, firstPage)

/**
 * Provides a [RxNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the page count in the response.
 *
 * @param ServiceResponse The response type returned by the service.
 * @param firstPage The first page number, defined by the service.
 */
fun <ServiceResponse : ListResponseWithPageCount<*>>
    RxPageFetcher<ServiceResponse>.toTotalPageCountNetworkDataSourceAdapter(
        firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
    ) = RxNetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(this, firstPage)
