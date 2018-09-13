package com.xmartlabs.fountain.coroutines.pagefetcher.totalentities

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.common.IntMockedListingCreator

class RefreshCacheModeNetworkDataSourceWithTotalEntityCountAdapterUnitTest
  : NetworkDataSourceWithTotalEntityCountAdapterUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)
}
