package com.xmartlabs.fountain.refresh

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter

class RefreshCacheModeUnitTest : RefreshUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)
}
