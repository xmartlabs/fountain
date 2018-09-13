package com.xmartlabs.fountain.coroutines.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.coroutines.FountainCoroutinesSupport
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntCacheDataSourceFactory
import com.xmartlabs.fountain.testutils.extensions.TestConstants
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import java.util.concurrent.Executors

object IntMockedListingCreator {
  //val IO_EXCECUTOR = InstantExecutor()
  val IO_EXCECUTOR = Executors.newSingleThreadExecutor()

  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int> {
    return FountainCoroutinesSupport.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceCoroutineDispatcher = IO_EXCECUTOR.asCoroutineDispatcher(),
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

    return FountainCoroutinesSupport.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        cachedDataSourceAdapter = dataSourceAdapter,
        ioServiceCoroutineDispatcher = IO_EXCECUTOR.asCoroutineDispatcher(),
        ioDatabaseCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }
}
