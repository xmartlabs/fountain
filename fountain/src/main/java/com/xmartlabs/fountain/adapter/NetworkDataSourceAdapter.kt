package com.xmartlabs.fountain.adapter

import android.support.annotation.CheckResult
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import io.reactivex.Single

interface PageFetcher<T> {
  @CheckResult
  fun fetchPage(page: Int, pageSize: Int): Single<out T>
}

interface NetworkDataSourceAdapter<T> : PageFetcher<T> {
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
