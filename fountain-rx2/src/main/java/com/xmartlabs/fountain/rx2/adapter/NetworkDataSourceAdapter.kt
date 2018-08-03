package com.xmartlabs.fountain.rx2.adapter

import android.support.annotation.CheckResult
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.adapter.KnownSizeResponseManager
import com.xmartlabs.fountain.common.FountainConstants
import io.reactivex.Single

/** It is used to fetch each page from the service using [RxJava](https://github.com/ReactiveX/RxJava). */
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
interface RxNetworkDataSourceAdapter<T> {
  val rxPageFetcher: RxPageFetcher<T>

  /** Returns `true` if the page [page] with a size [pageSize] can be fetched */
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}

object NetworkDataSourceAdapterFactory {
  fun <T, R : ListResponseWithEntityCount<T>> provideNetworkDataSourceWithTotalEntityCount(
      pageFetcher: RxPageFetcher<R>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RxNetworkDataSourceAdapter<R> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val rxPageFetcher: RxPageFetcher<R>
      get() = object : RxPageFetcher<R> {
        override fun fetchPage(page: Int, pageSize: Int): Single<R> =
            rxPageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalEntityResponseArrived(it) }

      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }

  fun <T, R :  ListResponseWithPageCount<T>> provideNetworkDataSourceWithTotalPageCount(
      pageFetcher: RxPageFetcher<R>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
  ) = object : RxNetworkDataSourceAdapter<R> {
    private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

    override val rxPageFetcher: RxPageFetcher<R>
      get() = object : RxPageFetcher<R> {
        override fun fetchPage(page: Int, pageSize: Int): Single<R> =
            rxPageFetcher.fetchPage(page, pageSize)
                .doOnSuccess { knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, it) }

      }

    override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)
  }
}
