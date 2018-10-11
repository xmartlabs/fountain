package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount

class KnownSizeResponseManager(private val firstPage: Int) {
  private var totalEntities: Long? = null

  fun onTotalEntityResponseArrived(response: ListResponseWithEntityCount<*>) {
    totalEntities = response.getEntityCount()
  }

  fun onTotalPageCountResponseArrived(requestedPageSize: Int, response: ListResponseWithPageCount<*>) {
    totalEntities = requestedPageSize * response.getPageCount()
  }

  fun canFetch(page: Int, pageSize: Int): Boolean {
    return totalEntities
        ?.let {
          val pageCount = if (firstPage == 0) page + 1 else page
          val firstEntityOfPagePosition = (pageCount - 1) * pageSize + 1
          return firstEntityOfPagePosition <= it
        }
        ?: true
  }
}
