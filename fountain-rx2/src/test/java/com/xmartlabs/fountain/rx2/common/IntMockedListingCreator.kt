package com.xmartlabs.fountain.rx2.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.rx2.FountainRx
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntCacheDataSourceFactory
import com.xmartlabs.fountain.testutils.TestConstants

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int> {
    return FountainRx.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceScheduler = InstantExecutor().toScheduler(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int> {
    val dataSourceAdapter: CachedDataSourceAdapter<Int, Int> = object : CachedDataSourceAdapter<Int, Int> {
      val sequentialIntCacheDataSourceFactory = IntCacheDataSourceFactory()

      override fun getDataSourceFactory() = sequentialIntCacheDataSourceFactory

      override fun saveEntities(response: List<Int>) {
        sequentialIntCacheDataSourceFactory.addData(response)
      }

      override fun dropEntities() {
        sequentialIntCacheDataSourceFactory.clearData()
      }

      override fun runInTransaction(transaction: () -> Unit) {
        transaction.invoke()
        sequentialIntCacheDataSourceFactory.invalidate()
      }
    }

    return FountainRx.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        cachedDataSourceAdapter = dataSourceAdapter,
        ioServiceScheduler = InstantExecutor().toScheduler(),
        ioDatabaseScheduler = InstantExecutor().toScheduler(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }
}
