package com.xmartlabs.fountain.rx2.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.rx2.adapter.NotPagedRxPageFetcher
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.adapter.RxPageFetcher
import com.xmartlabs.fountain.testutils.TestConstants
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.toListResponse
import com.xmartlabs.fountain.testutils.extensions.toListResponseEntityCount
import com.xmartlabs.fountain.testutils.extensions.toListResponsePageCount
import io.reactivex.Single

class MockedPageFetcher(var error: Boolean = false) : RxPageFetcher<ListResponse<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Single<ListResponse<Int>> =
      if (error) Single.error(IllegalStateException("Mocked error")) else generateServiceCall(page)
}

class NotPagedMockedPageFetcher(var error: Boolean = false) : NotPagedRxPageFetcher<ListResponse<Int>> {
  override fun fetchData(): Single<ListResponse<Int>> =
      if (error) {
        Single.error(IllegalStateException("Mocked error"))
      } else {
        generateServiceCall(TestConstants.DEFAULT_FIRST_PAGE)
      }
}

private fun generateServiceCall(page: Int) = generateSpecificIntPageResponseList(page)
    .toListResponse()
    .toSingle()

fun <T : ListResponse<*>> RxPageFetcher<T>.toInfiniteRxNetworkDataSourceAdapter() =
    object : RxNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toInfiniteRxNetworkDataSourceAdapter
      override fun canFetch(page: Int, pageSize: Int) = true
    }

class EntityCountMockedPageFetcher(private val entityCount: Long) : RxPageFetcher<ListResponseWithEntityCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseWithEntityCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .filter { it < entityCount }
          .toListResponseEntityCount(entityCount)
          .toSingle()
}

class PageCountMockedPageFetcher(private val pageCount: Long) : RxPageFetcher<ListResponseWithPageCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseWithPageCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .toListResponsePageCount(pageCount)
          .toSingle()
}

private fun <T> T.toSingle() = Single.just(this)
