package com.xmartlabs.fountain.rx2.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.rx2.FountainRx
import com.xmartlabs.fountain.rx2.adapter.NotPagedRxPageFetcher
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntMockedCachedDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = FountainRx.createNetworkListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      ioServiceScheduler = InstantExecutor().toScheduler(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNotPagedNetworkListing(
      notPagedRxPageFetcher: NotPagedRxPageFetcher<out ListResponse<Int>>
  ) = FountainRx.createNotPagedNetworkListing(
      notPagedRxPageFetcher = notPagedRxPageFetcher,
      ioServiceScheduler = InstantExecutor().toScheduler()
  )

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = FountainRx.createNetworkWithCacheSupportListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(),
      ioServiceScheduler = InstantExecutor().toScheduler(),
      ioDatabaseScheduler = InstantExecutor().toScheduler(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNotPagedNetworkWithCacheSupportListing(
      notPagedRxPageFetcher: NotPagedRxPageFetcher<out ListResponse<Int>>
  ) = FountainRx.createNotPagedNetworkWithCacheSupportListing(
      notPagedRxPageFetcher = notPagedRxPageFetcher,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(),
      ioServiceScheduler = InstantExecutor().toScheduler(),
      ioDatabaseScheduler = InstantExecutor().toScheduler()
  )
}
