package com.xmartlabs.fountain.adapter

import android.support.annotation.CheckResult
import android.support.annotation.NonNull
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount

interface NetworkResultListener<T> {
  fun onSuccess(@NonNull response: T)

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

abstract class NetworkDataSourceWithKnownEntityCountAdapter<T>(private val firstPage: Int = 1)
  : NetworkDataSourceAdapter<T> {
  var totalEntities: Long? = null

  override fun canFetch(page: Int, pageSize: Int): Boolean {
    if (totalEntities == null) {
      return true
    }

    val pageCount = if (firstPage == 0) page + 1 else page
    val firstEntityOfPagePosition = (pageCount - 1) * pageSize + 1
    return firstEntityOfPagePosition <= totalEntities!!
  }
}

/**
 * Provides a [NetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the entity count in the response.
 */
class NetworkDataSourceWithTotalEntityCountAdapter<T, R : ListResponseWithEntityCount<T>>(
    pageFetcher: PageFetcher<R>,
    firstPage: Int = 1
) : NetworkDataSourceWithKnownEntityCountAdapter<ListResponse<T>>(firstPage) {
  override val pageFetcher = object : PageFetcher<ListResponse<T>> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<ListResponse<T>>) =
        pageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<R> {
          override fun onSuccess(response: R) {
            totalEntities = response.getEntityCount()
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
class NetworkDataSourceWithTotalPageCountAdapter<T, R : ListResponseWithPageCount<T>>(
    pageFetcher: PageFetcher<R>,
    firstPage: Int = 1
) : NetworkDataSourceWithKnownEntityCountAdapter<ListResponse<T>>(firstPage) {
  override val pageFetcher = object : PageFetcher<ListResponse<T>> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<ListResponse<T>>) =
        pageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<R> {
          override fun onSuccess(response: R) {
            totalEntities = (pageSize.toLong() * response.getPageCount())
            networkResultListener.onSuccess(response)
          }

          override fun onError(t: Throwable) = networkResultListener.onError(t)
        })
  }
}
