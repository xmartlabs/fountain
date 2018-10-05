package com.xmartlabs.fountain.coroutines.pagefetcher.totalentities

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.common.IntMockedListingCreator

class RefreshNetworkModeNetworkDataSourceWithTotalEntityCountAdapterUnitTest
  : NetworkDataSourceWithTotalEntityCountAdapterUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
