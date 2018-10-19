package com.xmartlabs.fountain.networkstate

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import com.xmartlabs.fountain.testutils.extensions.sendPageResponse
import org.junit.Assert
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

    var state: NetworkState = NetworkState.Loading(TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)

    mockedNetworkDataSourceAdapter.sendPageResponse()

    state = NetworkState.Loaded(TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)

    listing.scrollToTheEnd()
    state = NetworkState.Loading(TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(state, listing.networkState.value)
  }

  @Test
  fun testSuccessOnePage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    var state: NetworkState = NetworkState.Loading(TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    state = NetworkState.Loaded(TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)
  }

  @Test
  fun testSuccessTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    var state: NetworkState = NetworkState.Loading(TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    state = NetworkState.Loaded(TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)

    listing.scrollToTheEnd()
    state = NetworkState.Loading(TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(listing.networkState.value, state)

    mockedNetworkDataSourceAdapter.sendPageResponse(1)
    state = NetworkState.Loaded(TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(listing.networkState.value, state)
  }

  @Test
  fun testErrorFirstPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.pageFetcher.onError(exception)
    val state = NetworkState.Error(exception, TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.networkState.value, state)
  }

  @Test
  fun testErrorInSecondPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)

    listing.scrollToTheEnd()
    assert(listing.networkState.value is NetworkState.Loading)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.pageFetcher.onError(exception)
    val state = NetworkState.Error(exception, TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(listing.networkState.value, state)
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
    mockedNetworkDataSourceAdapter.pageFetcher.onError(exception)
    assert(listing.networkState.value is NetworkState.Loaded)
    val state = NetworkState.Error(exception, TestConstants.DEFAULT_FIRST_PAGE,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, true, false)
    Assert.assertEquals(listing.refreshState.value, state)
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
