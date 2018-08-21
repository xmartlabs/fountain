package com.xmartlabs.fountain.refresh

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.Status
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.sendPageResponse
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

    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(Status.FAILED, listing.networkState.value!!.status)

    listing.refresh.invoke()
    Assert.assertEquals(NetworkState.LOADING, listing.refreshState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(Status.FAILED, listing.refreshState.value!!.status)

    listing.refresh.invoke()
    Assert.assertEquals(NetworkState.LOADING, listing.refreshState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(NetworkState.LOADED, listing.refreshState.value)
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }
}
