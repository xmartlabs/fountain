package com.xmartlabs.fountain.rx2.retry

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.common.IntMockedListingCreator

class RetryNetworkModeUnitTest : RetryUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
