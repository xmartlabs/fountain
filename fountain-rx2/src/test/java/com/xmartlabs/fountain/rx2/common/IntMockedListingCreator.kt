package com.xmartlabs.fountain.rx2.common

import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.rx2.FountainRxSupport
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter

object IntMockedListingCreator {
  private const val DEFAULT_FIRST_PAGE = 1
  internal const val DEFAULT_NETWORK_PAGE_SIZE = 20
  private val DEFAULT_PAGED_LIST_CONFIG = PagedList.Config.Builder()
      .setPageSize(DEFAULT_NETWORK_PAGE_SIZE)
      .setInitialLoadSizeHint(DEFAULT_NETWORK_PAGE_SIZE)
      .build()

  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int> {
    return FountainRxSupport.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceExecutor = InstantExecutor(),
        firstPage = DEFAULT_FIRST_PAGE,
        pagedListConfig = DEFAULT_PAGED_LIST_CONFIG
    )
  }

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>,
      numberOfErrors: Int = 0
  ): Listing<Int> {
    val dataSourceAdapter: CachedDataSourceAdapter<Int, Int> = object : CachedDataSourceAdapter<Int, Int> {
      var numberOfErrors = numberOfErrors
      val sequentialIntCacheDataSourceFactory = IntCacheDataSourceFactory()

      override fun getDataSourceFactory() = sequentialIntCacheDataSourceFactory

      override fun saveEntities(response: List<Int>) {
        sequentialIntCacheDataSourceFactory.addData(response)
      }

      override fun dropEntities() {
        if (this.numberOfErrors > 0) {
          this.numberOfErrors--
          throw IllegalStateException("${this.numberOfErrors} errors remaining.")
        } else{
          sequentialIntCacheDataSourceFactory.clearData()
        }
      }

      override fun runInTransaction(transaction: () -> Unit) {
        transaction.invoke()
        sequentialIntCacheDataSourceFactory.invalidate()
      }
    }

    return FountainRxSupport.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        cachedDataSourceAdapter = dataSourceAdapter,
        ioServiceExecutor = InstantExecutor(),
        ioDatabaseExecutor = InstantExecutor(),
        firstPage = DEFAULT_FIRST_PAGE,
        pagedListConfig = DEFAULT_PAGED_LIST_CONFIG
    )
  }
}