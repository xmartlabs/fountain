package com.xmartlabs.fountain.networkstate

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendPageResponse
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

    assert(listing.networkState.value is NetworkState.Loading)
  }

  @Test
  fun testLoadingTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()
    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)
    listing.pagedList.value!!.loadAround(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1)

    assert(listing.networkState.value is NetworkState.Loading)
  }

  @Test
  fun testSuccessOnePage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)
  }

  @Test
  fun testSuccessTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)

    listing.pagedList.value!!.loadAround(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1)
    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse(1)
    assert(listing.networkState.value is NetworkState.Loaded)
  }

  @Test
  fun testErrorFirstPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emitter?.onError(exception)
    assert(listing.networkState.value is NetworkState.Error)
  }

  @Test
  fun testErrorInSecondPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)

    listing.pagedList.value!!.loadAround(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE - 1)
    assert(listing.networkState.value is NetworkState.Loading)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emitter?.onError(exception)
    assert(listing.networkState.value is NetworkState.Error)
  }

  @Test
  fun testErrorRefresh() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)

    listing.refresh.invoke()
    assert(listing.refreshState.value is NetworkState.Loading)
    assert(listing.networkState.value is NetworkState.Loaded)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emitter?.onError(exception)
    assert(listing.networkState.value is NetworkState.Loaded)
    assert(listing.refreshState.value is NetworkState.Error)
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
