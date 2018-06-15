package com.xmartlabs.sample.model.service

import com.google.gson.annotations.SerializedName
import com.xmartlabs.fountain.ListResponseWithEntityCount

data class GhListResponse<T>(
    @SerializedName("total_count") private val totalCount: Long,
    private val items: List<T>)
  : ListResponseWithEntityCount<T> {

  override fun getEntityCount() = totalCount

  override fun getElements() = items
}
