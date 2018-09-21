package com.xmartlabs.fountain.testutils.extensions

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount

fun <T> List<T>.toListResponseEntityCount(entityCount: Long): ListResponseWithEntityCount<T> =
    object : ListResponseWithEntityCount<T> {
      override fun getEntityCount() = entityCount

      override fun getElements(): List<T> = this@toListResponseEntityCount
    }

fun <T> List<T>.toListResponsePageCount(pageCount: Long): ListResponseWithPageCount<T> =
    object : ListResponseWithPageCount<T> {
      override fun getPageCount() = pageCount

      override fun getElements(): List<T> = this@toListResponsePageCount
    }
