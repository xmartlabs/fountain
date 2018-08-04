package com.xmartlabs.fountain.retry

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter

class RetryNetworkModeUnitTest : RetryUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
