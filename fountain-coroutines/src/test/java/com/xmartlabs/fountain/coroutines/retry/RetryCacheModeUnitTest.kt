package com.xmartlabs.fountain.coroutines.retry

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.common.IntMockedListingCreator

class RetryCacheModeUnitTest : RetryUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)
}
