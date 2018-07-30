package com.xmartlabs.fountain.rx2.adapter

import android.support.annotation.CheckResult
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.adapter.NetworkDataSourceWithTotalEntityCountAdapter
import com.xmartlabs.fountain.adapter.NetworkDataSourceWithTotalPageCountAdapter
import io.reactivex.Single

/**
 * Created by matias on 21/07/18.
 */

/** It is used to fetch each page from the service. */
interface RxPageFetcher<T> {
  /**
   * Fetches the page [page] with a size [pageSize] from the service.
   *
   * @param page The page number to fetch.
   * @param pageSize The page size to fetch.
   * @return A [Single] of the type [T] that represent the service call.
   */
  fun fetchPage(page: Int, pageSize: Int): Single<T>
}

/** It is used to handle the paging state */
interface RxNetworkDataSourceAdapter<T> : RxPageFetcher<T> {
  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}

object NetworkDataSourceAdapterProvider {
  fun <T> provideNetworkDataSourceWithTotalEntityCount(
      pageFetcher: RxPageFetcher<out ListResponseWithEntityCount<T>>,
      firstPage: Int = 1
  ) = NetworkDataSourceWithTotalEntityCountAdapter(
      pageFetcher = pageFetcher.toPageFetcher(),
      firstPage = firstPage
  )

  fun <T> provideNetworkDataSourceWithTotalPageCount(
      pageFetcher: RxPageFetcher<out ListResponseWithPageCount<T>>,
      firstPage: Int = 1
  ) = NetworkDataSourceWithTotalPageCountAdapter(
      pageFetcher = pageFetcher.toPageFetcher(),
      firstPage = firstPage
  )
}
