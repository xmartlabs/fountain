package com.xmartlabs.fountain.retrofit.pagefetcher.notpaged

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.retrofit.adapter.NotPagedRetrifitPageFetcher
import com.xmartlabs.fountain.retrofit.common.IntMockedListingCreator

class NotPagedNetworkModeUnitTest : NotPagedUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: NotPagedRetrifitPageFetcher<ListResponse<Int>>) =
      IntMockedListingCreator.createNotPagedNetworkListing(mockedNetworkDataSourceAdapter)
}
