package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.feature.cachednetwork.CachedNetworkListingCreator
import com.xmartlabs.fountain.feature.network.NetworkPagedListingCreator
import com.xmartlabs.fountain.testutils.InstantExecutor
import com.xmartlabs.fountain.testutils.IntMockedCachedDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants

object IntMockedListingCreator {
  fun createNetworkListing(
      mockedNetworkDataSourceAdapter: BaseNetworkDataSourceAdapter<out ListResponse<Int>>
  ) = NetworkPagedListingCreator.createListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      ioServiceExecutor = InstantExecutor(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )

  fun createNetworkWithCacheSupportListing(
      mockedNetworkDataSourceAdapter: BaseNetworkDataSourceAdapter<out ListResponse<Int>>,
      numberOfErrors: Int = 0
  ) = CachedNetworkListingCreator.createListing(
      networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
      cachedDataSourceAdapter = IntMockedCachedDataSourceAdapter(numberOfErrors),
      ioServiceExecutor = InstantExecutor(),
      ioDatabaseExecutor = InstantExecutor(),
      firstPage = TestConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig = TestConstants.DEFAULT_PAGED_LIST_CONFIG
  )
}
