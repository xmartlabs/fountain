package com.xmartlabs.fountain.retrofit.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.retrofit.FountainRetrofit
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntCacheDataSourceFactory
import com.xmartlabs.fountain.testutils.TestConstants

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int> {
    return FountainRetrofit.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceExecutor = InstantExecutor(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>
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

    return FountainRetrofit.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        cachedDataSourceAdapter = dataSourceAdapter,
        ioServiceExecutor = InstantExecutor(),
        ioDatabaseExecutor = InstantExecutor(),
        firstPage = TestConstants.DEFAULT_FIRST_PAGE,
        pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
    )
  }
}
