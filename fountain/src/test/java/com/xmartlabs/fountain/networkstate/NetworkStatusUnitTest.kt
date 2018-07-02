package com.xmartlabs.fountain.networkstate

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendPageResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class NetworkStatusUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testLoadingOnePage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)
  }

  @Test
  fun testLoadingTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()
    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    listing.pagedList.value!!.loadAround(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1)

    assertEquals(NetworkState.LOADING, listing.networkState.value)
  }

  @Test
  fun testSuccessOnePage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)
  }

  @Test
  fun testSuccessTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    listing.pagedList.value!!.loadAround(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1)
    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse(1)
    assertEquals(NetworkState.LOADED, listing.networkState.value)
  }

  @Test
  fun testErrorFirstPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emitter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)
  }

  @Test
  fun testErrorInSecondPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    listing.pagedList.value!!.loadAround(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1)
    assertEquals(NetworkState.LOADING, listing.networkState.value)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emitter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)
  }

  @Test
  fun testErrorRefresh() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    listing.refresh.invoke()
    assertEquals(NetworkState.LOADING, listing.refreshState.value)
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emitter?.onError(exception)
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    assertEquals(NetworkState.error(exception), listing.refreshState.value)
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
