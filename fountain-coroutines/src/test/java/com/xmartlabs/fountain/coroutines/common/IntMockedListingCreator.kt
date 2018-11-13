package com.xmartlabs.fountain.coroutines.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.coroutines.FountainCoroutines
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.adapter.NotPagedCoroutinePageFetcher
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntMockedCachedDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants
import kotlinx.coroutines.asCoroutineDispatcher

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = FountainCoroutines.createNetworkListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      ioServiceCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNotPagedNetworkListing(
      notPagedCoroutinePageFetcher: NotPagedCoroutinePageFetcher<out ListResponse<Int>>
  ) = FountainCoroutines.createNotPagedNetworkListing(
      notPagedCoroutinePageFetcher = notPagedCoroutinePageFetcher,
      ioServiceCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher()
  )

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = FountainCoroutines.createNetworkWithCacheSupportListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(),
      ioServiceCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
      ioDatabaseCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNotPagedNetworkWithCacheSupportListing(
      notPagedCoroutinePageFetcher: NotPagedCoroutinePageFetcher<out ListResponse<Int>>
  ) = FountainCoroutines.createNotPagedNetworkWithCacheSupportListing(
      notPagedCoroutinePageFetcher = notPagedCoroutinePageFetcher,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(),
      ioServiceCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher(),
      ioDatabaseCoroutineDispatcher = InstantExecutor().asCoroutineDispatcher()
  )
}
