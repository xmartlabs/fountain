package com.xmartlabs.fountain.common.extensions

import com.xmartlabs.fountain.Fountain
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter

fun MockedNetworkDataSourceAdapter<ListResponse<Int>>.sendPageResponse(page: Int = 0) {
  emmiter?.onSuccess(object : ListResponse<Int> {
    override fun getElements() = generateIntPageResponseList(page)
  })
}

fun generateIntPageResponseList(vararg pages: Int): List<Int> {
  return pages
      .distinct()
      .sorted()
      .flatMap {
        val start = it * Fountain.DEFAULT_NETWORK_PAGE_SIZE
        val end = (it + 1) * Fountain.DEFAULT_NETWORK_PAGE_SIZE - 1
        (start..end).toList()
      }
}
