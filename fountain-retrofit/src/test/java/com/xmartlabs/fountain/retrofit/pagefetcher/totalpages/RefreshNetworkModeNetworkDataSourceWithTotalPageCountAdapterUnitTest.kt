package com.xmartlabs.fountain.retrofit.pagefetcher.totalpages

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.common.IntMockedListingCreator

class RefreshNetworkModeNetworkDataSourceWithTotalPageCountAdapterUnitTest
  : NetworkDataSourceWithTotalPageCountAdapterUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
