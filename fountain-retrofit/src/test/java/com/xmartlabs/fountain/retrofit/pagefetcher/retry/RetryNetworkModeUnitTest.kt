package com.xmartlabs.fountain.retrofit.pagefetcher.retry

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.common.IntMockedListingCreator

class RetryNetworkModeUnitTest : RetryUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkListing(mockedNetworkDataSourceAdapter)
}
