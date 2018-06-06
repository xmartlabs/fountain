package com.xmartlabs.sample.model.service

import com.xmartlabs.xlpagingbypagenumber.ListResponseWithEntityCount

data class GhListResponse<T>(val total_count: Long, private val items: List<T>) : ListResponseWithEntityCount<T> {
  override fun getEntityCount() = total_count

  override fun getElements(): List<T> = items
}
