package com.xmartlabs.fountain.common.extensions

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.MockedNetworkDataSourcePageFetcher

fun MockedNetworkDataSourceAdapter<ListResponse<Int>>.sendPageResponse(page: Int = 0) {
  emitter?.onSuccess(object : ListResponse<Int> {
    override fun getElements() = generateIntPageResponseList(page)
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>.sendListResponseWithEntityCountResponse(
    entityCount: Long,
    page: Int = 0) {
  emitter?.onSuccess(object : ListResponseWithEntityCount<Int> {
    override fun getEntityCount() = entityCount
    override fun getElements() = generateIntPageResponseList(page)
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>.sendListResponseWithEntityCountResponse(
    entityCount: Long,
    elements: List<Int>) {
  emitter?.onSuccess(object : ListResponseWithEntityCount<Int> {
    override fun getEntityCount() = entityCount
    override fun getElements() = elements
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>.sendListResponseWithPageCountResponse(
    pageCount: Long,
    page: Int = 0) {
  emitter?.onSuccess(object : ListResponseWithPageCount<Int> {
    override fun getPageCount() = pageCount

    override fun getElements() = generateIntPageResponseList(page)
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>.sendListResponseWithPageCountResponse(
    pageCount: Long,
    elements: List<Int>) {
  emitter?.onSuccess(object : ListResponseWithPageCount<Int> {
    override fun getPageCount() = pageCount

    override fun getElements() = elements
  })
}

fun generateIntPageResponseList(vararg pages: Int): List<Int> {
  return pages
      .distinct()
      .sorted()
      .flatMap {
        val start = it * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE
        val end = (it + 1) * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1
        (start..end).toList()
      }
}
