package com.xmartlabs.fountain.testutils.extensions

import android.arch.paging.PagedList
import java.util.AbstractList

fun <T> PagedList<T>.getList(): List<T?> =
    javaClass.superclass.getDeclaredField("mStorage")
        .let { field ->
          field.isAccessible = true
          val abstractList = field.get(this) as AbstractList<T?>
          while (abstractList.contains(null)) {
            val indexOfFirst = abstractList.indexOfFirst { it == null }
            loadAround(indexOfFirst)
          }
          return abstractList
        }

fun <T> PagedList<T>.scrollToTheEnd() =  loadAround(size - 1)
