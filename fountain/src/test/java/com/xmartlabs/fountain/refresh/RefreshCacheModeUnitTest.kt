package com.xmartlabs.fountain.refresh

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.common.extensions.getPagedList
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendPageResponse
import org.junit.Assert
import org.junit.Test

class RefreshCacheModeUnitTest : RefreshUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)

  @Test
  fun testRefreshDataWithDataSourceSaveError() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter, 2)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value!! is NetworkState.Error)

    listing.refresh.invoke()
    assert( listing.refreshState.value is NetworkState.Loading)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value!! is NetworkState.Error)

    listing.refresh.invoke()
    assert( listing.refreshState.value is NetworkState.Loading)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert( listing.refreshState.value is NetworkState.Success)
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }
}
