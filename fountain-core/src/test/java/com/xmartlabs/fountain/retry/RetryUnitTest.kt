package com.xmartlabs.fountain.retry

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import com.xmartlabs.fountain.testutils.extensions.sendPageResponse
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class RetryUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testRetryFirstCall() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.pageFetcher.onError(exception)
    assert(listing.networkState.value is NetworkState.Error)

    listing.retry.invoke()
    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())
  }

  @Test
  fun testRetrySecondFirstCall() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())
    listing.scrollToTheEnd()

    val exception = Exception()
    mockedNetworkDataSourceAdapter.pageFetcher.onError(exception)
    var networkState: NetworkState = NetworkState.Error(exception, TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(listing.networkState.value, networkState)

    listing.retry.invoke()
    networkState = NetworkState.Loading(TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(listing.networkState.value, networkState)
    mockedNetworkDataSourceAdapter.sendPageResponse(1)

    networkState = NetworkState.Loaded(TestConstants.DEFAULT_FIRST_PAGE + 1,
        TestConstants.DEFAULT_NETWORK_PAGE_SIZE, false, false)
    Assert.assertEquals(listing.networkState.value, networkState)

    assertEquals(generateSpecificIntPageResponseList(0, 1), listing.getPagedList())
  }

  @Test
  fun testRetryTwoTimes() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())
    listing.scrollToTheEnd()

    val exception = Exception()
    mockedNetworkDataSourceAdapter.pageFetcher.onError(exception)
    assert(listing.networkState.value is NetworkState.Error)

    listing.retry.invoke()
    assert(listing.networkState.value is NetworkState.Loading)
    mockedNetworkDataSourceAdapter.sendPageResponse(1)
    assert(listing.networkState.value is NetworkState.Loaded)

    listing.retry.invoke()
    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateSpecificIntPageResponseList(0, 1), listing.getPagedList())
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
