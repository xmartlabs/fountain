package com.xmartlabs.fountain.retry

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.Status
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.common.extensions.getPagedList
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendPageResponse
import org.junit.Assert
import org.junit.Test

class RetryCacheModeUnitTest : RetryUnitTest() {
  override fun createListing(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>) =
      IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter)

  @Test
  fun testRetryFirstCallWithDataSourceSaveError() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter, 2)
        .mockLifecycleEvents()

    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(Status.FAILED, listing.networkState.value!!.status)

    listing.retry.invoke()
    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(Status.FAILED, listing.networkState.value!!.status)

    listing.retry.invoke()
    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(NetworkState.LOADED, listing.networkState.value)
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }

  @Test
  fun testRetryFirstCallWithDataSourceSaveErrorAndRetryError() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = IntMockedListingCreator.createNetworkWithCacheSupportListing(mockedNetworkDataSourceAdapter, 2)
        .mockLifecycleEvents()

    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(Status.FAILED, listing.networkState.value!!.status)

    listing.retry.invoke()
    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(Status.FAILED, listing.networkState.value!!.status)

    listing.retry.invoke()
    Assert.assertEquals(NetworkState.LOADING, listing.networkState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse()
    Assert.assertEquals(NetworkState.LOADED, listing.networkState.value)
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }
}
