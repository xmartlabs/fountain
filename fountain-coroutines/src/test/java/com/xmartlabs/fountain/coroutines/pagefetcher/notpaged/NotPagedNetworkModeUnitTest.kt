package com.xmartlabs.fountain.coroutines.pagefetcher.notpaged

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.coroutines.adapter.NotPagedCoroutinePageFetcher
import com.xmartlabs.fountain.coroutines.common.IntMockedListingCreator

class NotPagedNetworkModeUnitTest : NotPagedUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: NotPagedCoroutinePageFetcher<ListResponse<Int>>) =
      IntMockedListingCreator.createNotPagedNetworkListing(mockedNetworkDataSourceAdapter)
}
