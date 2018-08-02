package com.xmartlabs.fountain.common

import android.arch.paging.PagedList
import java.util.concurrent.Executors

object FountainConstants{
  val NETWORK_EXECUTOR by lazy { Executors.newFixedThreadPool(5) }
  val DATABASE_EXECUTOR by lazy { Executors.newSingleThreadExecutor() }

  const val DEFAULT_FIRST_PAGE = 1
  const val DEFAULT_NETWORK_PAGE_SIZE = 20
  val DEFAULT_PAGED_LIST_CONFIG = PagedList.Config.Builder()
      .setPageSize(DEFAULT_NETWORK_PAGE_SIZE)
      .build()

}
