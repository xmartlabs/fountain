package com.xmartlabs.fountain.refresh

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourceAdapter

class RefreshNetworkModeUnitTest : RefreshUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
