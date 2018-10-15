package com.xmartlabs.fountain.retrofit.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.retrofit.FountainRetrofit
import com.xmartlabs.fountain.retrofit.adapter.NotPagedRetrifitPageFetcher
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntMockedCachedDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = FountainRetrofit.createNetworkListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      ioServiceExecutor = InstantExecutor(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNotPagedNetworkListing(
      notPagedRetrifitPageFetcher: NotPagedRetrifitPageFetcher<out ListResponse<Int>>
  ) = FountainRetrofit.createNotPagedNetworkListing(
      notPagedRetrifitPageFetcher = notPagedRetrifitPageFetcher,
      ioServiceExecutor = InstantExecutor()
  )

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = FountainRetrofit.createNetworkWithCacheSupportListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(),
      ioServiceExecutor = InstantExecutor(),
      ioDatabaseExecutor = InstantExecutor(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNotPagedNetworkWithCacheSupportListing(
      notPagedRetrifitPageFetcher: NotPagedRetrifitPageFetcher<out ListResponse<Int>>
  ) = FountainRetrofit.createNotPagedNetworkWithCacheSupportListing(
      notPagedRetrifitPageFetcher = notPagedRetrifitPageFetcher,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(),
      ioServiceExecutor = InstantExecutor(),
      ioDatabaseExecutor = InstantExecutor()
  )
}
