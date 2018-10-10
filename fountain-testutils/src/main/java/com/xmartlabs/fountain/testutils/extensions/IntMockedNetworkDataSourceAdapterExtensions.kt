package com.xmartlabs.fountain.testutils.extensions

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourcePageFetcher
import com.xmartlabs.fountain.testutils.TestConstants

fun MockedNetworkDataSourceAdapter<ListResponse<Int>>.sendPageResponse(page: Int = 0) {
  pageFetcher.onSuccess(object : ListResponse<Int> {
    override fun getElements() = generateSpecificIntPageResponseList(page)
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>.sendListResponseWithEntityCountResponse(
    entityCount: Long,
    page: Int = 0
) {
  onSuccess(object : ListResponseWithEntityCount<Int> {
    override fun getEntityCount() = entityCount
    override fun getElements() = generateSpecificIntPageResponseList(page + FountainConstants.DEFAULT_FIRST_PAGE)
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>.sendListResponseWithEntityCountResponse(
    entityCount: Long,
    elements: List<Int>
) {
  onSuccess(object : ListResponseWithEntityCount<Int> {
    override fun getEntityCount() = entityCount
    override fun getElements() = elements
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>.sendListResponseWithPageCountResponse(
    pageCount: Long,
    page: Int = 0
) {
  onSuccess(object : ListResponseWithPageCount<Int> {
    override fun getPageCount() = pageCount

    override fun getElements() = generateSpecificIntPageResponseList(page)
  })
}

fun MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>.sendListResponseWithPageCountResponse(
    pageCount: Long,
    elements: List<Int>
) {
  onSuccess(object : ListResponseWithPageCount<Int> {
    override fun getPageCount() = pageCount

    override fun getElements() = elements
  })
}

fun generateSpecificIntPageResponseList(vararg pages: Int): List<Int> {
  return pages
      .map { it - TestConstants.DEFAULT_FIRST_PAGE }
      .distinct()
      .sorted()
      .flatMap {
        val start = it * FountainConstants.DEFAULT_NETWORK_PAGE_SIZE
        val end = (it + 1) * FountainConstants.DEFAULT_NETWORK_PAGE_SIZE - 1
        (start..end).toList()
      }
}

fun generateIntPageResponseList(numberOfRequested: Int): List<Int> {
  return (0..(numberOfRequested - 1))
      .map { it + TestConstants.DEFAULT_FIRST_PAGE }
      .flatMap { generateSpecificIntPageResponseList(it) }
}
