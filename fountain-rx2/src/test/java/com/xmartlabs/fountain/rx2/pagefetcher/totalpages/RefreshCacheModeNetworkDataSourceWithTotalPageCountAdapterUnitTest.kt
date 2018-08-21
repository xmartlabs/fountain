package com.xmartlabs.fountain.rx2.pagefetcher.totalpages

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.common.IntMockedListingCreator

class RefreshCacheModeNetworkDataSourceWithTotalPageCountAdapterUnitTest
  : NetworkDataSourceWithTotalPageCountAdapterUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)
}
