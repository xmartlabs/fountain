package com.xmartlabs.fountain.testutils

import android.arch.paging.PagedList

object TestConstants {
  const val DEFAULT_FIRST_PAGE = 1
  const val DEFAULT_NETWORK_PAGE_SIZE = 20
  val DEFAULT_PAGED_LIST_CONFIG = PagedList.Config.Builder()
      .setPageSize(DEFAULT_NETWORK_PAGE_SIZE)
      .setInitialLoadSizeHint(DEFAULT_NETWORK_PAGE_SIZE)
      .build()
}
