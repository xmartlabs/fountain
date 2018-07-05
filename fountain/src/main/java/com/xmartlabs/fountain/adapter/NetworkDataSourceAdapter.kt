package com.xmartlabs.fountain.adapter

import android.support.annotation.CheckResult
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import io.reactivex.Single

/** It is used to fetch each page from the service. */
interface PageFetcher<T> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Single] of the type [T] that represent the service call.
   */
  @CheckResult
  fun fetchPage(page: Int, pageSize: Int): Single<out T>
}

/** It is used to handle the paging state */
interface NetworkDataSourceAdapter<T> : PageFetcher<T> {
  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}

abstract class NetworkDataSourceWithKnownEntityCountAdapter<T>(private val firstPage: Int = 1)
  : NetworkDataSourceAdapter<ListResponse<T>> {
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
class NetworkDataSourceWithTotalEntityCountAdapter<T>(
    private val pageFetcher: PageFetcher<out ListResponseWithEntityCount<T>>,
    firstPage: Int = 1
) : NetworkDataSourceWithKnownEntityCountAdapter<T>(firstPage) {

  @CheckResult
  override fun fetchPage(page: Int, pageSize: Int): Single<out ListResponseWithEntityCount<T>> {
    return pageFetcher.fetchPage(page = page, pageSize = pageSize)
        .doOnSuccess { response -> totalEntities = response.getEntityCount() }
  }
}

/**
 * Provides a [NetworkDataSourceAdapter] implementation of a [ListResponseWithPageCount] response.
 * It is used when the service returns the page count in the response.
 */
class NetworkDataSourceWithTotalPageCountAdapter<T>(
    private val pageFetcher: PageFetcher<out ListResponseWithPageCount<T>>,
    firstPage: Int = 1
) : NetworkDataSourceWithKnownEntityCountAdapter<T>(firstPage) {

  @CheckResult
  override fun fetchPage(page: Int, pageSize: Int): Single<out ListResponseWithPageCount<T>> {
    return pageFetcher.fetchPage(page = page, pageSize = pageSize)
        .doOnSuccess { response -> totalEntities = (pageSize.toLong() * response.getPageCount()) }
  }
}
