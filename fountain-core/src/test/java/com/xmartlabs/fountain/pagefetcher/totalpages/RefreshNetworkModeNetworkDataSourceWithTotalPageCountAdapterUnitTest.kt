package com.xmartlabs.fountain.pagefetcher.totalpages

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.common.IntMockedListingCreator

class RefreshNetworkModeNetworkDataSourceWithTotalPageCountAdapterUnitTest
  : NetworkDataSourceWithTotalPageCountAdapterUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: NetworkDataSourceAdapter<out ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
