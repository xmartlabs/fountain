package com.xmartlabs.fountain.networkstate

import com.xmartlabs.fountain.Fountain
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.common.InstantExecutor
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter

class NetworkStatusNetworkModeUnitTest : NetworkStatusUnitTest() {
  override fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int> {
    return Fountain.createNetworkListing(
        networkDataSourceAdapter = mockedNetworkDataSourceAdapter,
        ioServiceExecutor = InstantExecutor()
    )
  }
}
