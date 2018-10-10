package com.xmartlabs.fountain.coroutines.adapter

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.KnownSizeResponseManager
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.runBlocking

/** A [CoroutineNetworkDataSourceAdapter] factory */
object CoroutineNetworkDataSourceAdapterFactory {
  /**
   * Provides a [CoroutineNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the entity count in the response.
   *
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <ListResponseValue : ListResponseWithEntityCount<*>> fromTotalEntityCountListResponse(
      pageFetcher: CoroutinePageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : CoroutineNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val pageFetcher: CoroutinePageFetcher<ListResponseValue>
      get() = object : CoroutinePageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Deferred<ListResponseValue> {
          val deferred = CompletableDeferred<ListResponseValue>()
          runBlocking {
            try {
              val responseValue = pageFetcher.fetchPage(page, pageSize).await()
              knownSizeResponseManager.onTotalEntityResponseArrived(responseValue)
              deferred.complete(responseValue)
            } catch (throwable: Throwable) {
              deferred.completeExceptionally(throwable)
            }
          }
          return deferred
        }
      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }

  /**
   * Provides a [CoroutineNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
   * It is used when the service returns the page count in the response.
   *
   * @param ListResponseValue The response type that the service returns.
   * @param pageFetcher It is used to fetch each page from the service.
   * @param firstPage The first page number, defined by the service.
   */
  fun <ListResponseValue : ListResponseWithPageCount<*>> fromTotalPageCountListResponse(
      pageFetcher: CoroutinePageFetcher<ListResponseValue>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : CoroutineNetworkDataSourceAdapter<ListResponseValue> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val pageFetcher: CoroutinePageFetcher<ListResponseValue>
      get() = object : CoroutinePageFetcher<ListResponseValue> {
        override fun fetchPage(page: Int, pageSize: Int): Deferred<ListResponseValue> {
          val deferred = CompletableDeferred<ListResponseValue>()
          runBlocking {
            try {
              val responseValue = pageFetcher.fetchPage(page, pageSize).await()
              knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, responseValue)
              deferred.complete(responseValue)
            } catch (throwable: Throwable) {
              deferred.completeExceptionally(throwable)
            }
          }
          return deferred
        }
      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }
}

/**
 * Provides a [CoroutineNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the entity count in the response.
 *
 * @param ServiceResponse The response type returned by the service.
 * @param firstPage The first page number, defined by the service.
 */
fun <ServiceResponse : ListResponseWithEntityCount<*>>
    CoroutinePageFetcher<ServiceResponse>.toTotalEntityCountNetworkDataSourceAdapter(
        firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
    ) = CoroutineNetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(this, firstPage)

/**
 * Provides a [CoroutineNetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the page count in the response.
 *
 * @param ServiceResponse The response type returned by the service.
 * @param firstPage The first page number, defined by the service.
 */
fun <ServiceResponse : ListResponseWithPageCount<*>>
    CoroutinePageFetcher<ServiceResponse>.toTotalPageCountNetworkDataSourceAdapter(
        firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
    ) = CoroutineNetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(this, firstPage)
