package com.xmartlabs.fountain.coroutines.pagefetcher.notpaged

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.coroutines.adapter.NotPagedCoroutinePageFetcher
import com.xmartlabs.fountain.coroutines.common.IntMockedListingCreator

class NotPagedCacheModeUnitTest : NotPagedUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: NotPagedCoroutinePageFetcher<ListResponse<Int>>) =
      IntMockedListingCreator.createNotPagedNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)
}
