package com.xmartlabs.fountain.coroutines.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.coroutines.FountainCoroutines
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntCacheDataSourceFactory
import com.xmartlabs.fountain.testutils.TestConstants
import kotlinx.coroutines.experimental.asCoroutineDispatcher

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int> {
    return FountainCoroutines.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>
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

    return FountainCoroutines.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        cachedDataSourceAdapter = dataSourceAdapter,
        ioServiceCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
        ioDatabaseCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }
}
