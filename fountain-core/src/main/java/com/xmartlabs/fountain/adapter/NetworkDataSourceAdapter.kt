package com.xmartlabs.fountain.adapter

import android.support.annotation.CheckResult
import android.support.annotation.NonNull
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.KnownSizeResponseManager


/** It is used to get notify the service response. */
interface NetworkResultListener<T> {
  /** Invoked when the service returned a valid response. */
  fun onSuccess(@NonNull response: T)
  /** Invoked when the service request thrown an error. */
  fun onError(@NonNull t: Throwable)
}

/** It is used to fetch each page from the service. */
interface PageFetcher<T> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Single] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>)
}

/** It is used to handle the paging state */
interface NetworkDataSourceAdapter<T> {
  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean

  val pageFetcher: PageFetcher<T>
}

/**
 * Provides a [NetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the entity count in the response.
 */
internal class NetworkDataSourceWithTotalEntityCountAdapter<T, R : ListResponseWithEntityCount<T>>(
    pageFetcher: PageFetcher<R>,
    firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
) : NetworkDataSourceAdapter<ListResponse<T>> {
  private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

  override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)

  override val pageFetcher = object : PageFetcher<ListResponse<T>> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<ListResponse<T>>) =
        pageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<R> {
          override fun onSuccess(response: R) {
            knownSizeResponseManager.onTotalEntityResponseArrived(response)
            networkResultListener.onSuccess(response)
          }

          override fun onError(t: Throwable) = networkResultListener.onError(t)
        })
  }
}

/**
 * Provides a [NetworkDataSourceAdapter] implementation of a [ListResponseWithPageCount] response.
 * It is used when the service returns the page count in the response.
 */
internal class NetworkDataSourceWithTotalPageCountAdapter<T, R : ListResponseWithPageCount<T>>(
    pageFetcher: PageFetcher<R>,
    firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
) : NetworkDataSourceAdapter<ListResponse<T>> {
  private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

  override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)

  override val pageFetcher = object : PageFetcher<ListResponse<T>> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<ListResponse<T>>) =
        pageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<R> {
          override fun onSuccess(response: R) {
            knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, response)
            networkResultListener.onSuccess(response)
          }

          override fun onError(t: Throwable) = networkResultListener.onError(t)
        })
  }
}