package com.xmartlabs.xlpagingbypagenumber.fetcher

import android.support.annotation.CheckResult
import com.xmartlabs.xlpagingbypagenumber.ListResponse
import com.xmartlabs.xlpagingbypagenumber.ListResponseWithEntityCount
import com.xmartlabs.xlpagingbypagenumber.ListResponseWithPageCount
import io.reactivex.Single

typealias ListResponsePagingHandler<T> = PagingHandler<ListResponse<T>>

interface PageFetcher<T> {
  @CheckResult
  fun fetchPage(page: Int, pageSize: Int): Single<out T>
}

interface PagingHandler<T> : PageFetcher<T> {
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}

abstract class PagingHandlerWithKnownEntityCount<T>(private val firstPage: Int = 1) : ListResponsePagingHandler<T> {
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

class PagingHandlerWithTotalEntityCount<T>(
    private val firstPage: Int = 1,
    private val pageFetcher: PageFetcher<out ListResponseWithEntityCount<T>>
) : PagingHandlerWithKnownEntityCount<T>(firstPage) {

  @CheckResult
  override fun fetchPage(page: Int, pageSize: Int): Single<out ListResponseWithEntityCount<T>> {
    return pageFetcher.fetchPage(page = page, pageSize = pageSize)
        .doOnSuccess({ response -> totalEntities = response.getEntityCount() })
  }
}

class PagingHandlerWithTotalPageCount<T>(
    private val firstPage: Int = 1,
    private val pageFetcher: PageFetcher<out ListResponseWithPageCount<T>>
) : PagingHandlerWithKnownEntityCount<T>(firstPage) {

  @CheckResult
  override fun fetchPage(page: Int, pageSize: Int): Single<out ListResponseWithPageCount<T>> {
    return pageFetcher.fetchPage(page = page, pageSize = pageSize)
        .doOnSuccess({ response -> totalEntities = (pageSize.toLong() * response.getPageCount()) })
  }
}
