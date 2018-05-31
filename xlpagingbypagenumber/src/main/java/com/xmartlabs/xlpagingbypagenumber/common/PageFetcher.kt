package com.xmartlabs.xlpagingbypagenumber.common

import android.support.annotation.CheckResult
import com.xmartlabs.xlpagingbypagenumber.ListResponse
import io.reactivex.Single


interface ListResponsePageFetcher<T> : PageFetcher<ListResponse<T>> {
  @CheckResult
  override fun getPage(page: Int, pageSize: Int): Single<out ListResponse<T>>
}

interface PageFetcher<T> {
  @CheckResult
  fun getPage(page: Int, pageSize: Int): Single<out T>

  fun canFetch(page: Int): Boolean
}
