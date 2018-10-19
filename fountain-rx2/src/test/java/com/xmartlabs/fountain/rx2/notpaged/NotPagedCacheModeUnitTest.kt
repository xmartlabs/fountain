package com.xmartlabs.fountain.rx2.notpaged

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.rx2.adapter.NotPagedRxPageFetcher
import com.xmartlabs.fountain.rx2.common.IntMockedListingCreator

class NotPagedCacheModeUnitTest : NotPagedUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: NotPagedRxPageFetcher<ListResponse<Int>>) =
      IntMockedListingCreator.createNotPagedNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)
}
