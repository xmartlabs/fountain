package com.xmartlabs.sample.model.service

import com.xmartlabs.xlpagingbypagenumber.ListResponse

data class GhListResponse<T>(val total_count: Long, private val items: List<T>) : ListResponse<T> {
  override fun getElements(): List<T> = items
}
