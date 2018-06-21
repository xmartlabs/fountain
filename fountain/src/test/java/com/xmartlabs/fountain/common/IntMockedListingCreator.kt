package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.Fountain
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int> {
    return Fountain.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceExecutor = InstantExecutor()
    )
  }

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int> {
    val dataSourceAdapter: CachedDataSourceAdapter<Int> = object : CachedDataSourceAdapter<Int> {
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

    return Fountain.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        cachedDataSourceAdapter = dataSourceAdapter,
        ioServiceExecutor = InstantExecutor(),
        ioDatabaseExecutor = InstantExecutor()
    )
  }
}
