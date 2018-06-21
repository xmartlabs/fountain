package com.xmartlabs.fountain.retry

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.common.extensions.getPagedList
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendPageResponse
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

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emmiter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)

    listing.retry.invoke()
    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }

  @Test
  fun testRetrySecondFirstCall() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emmiter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)

    listing.retry.invoke()
    assertEquals(NetworkState.LOADING, listing.networkState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse(1)
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())
  }

  @Test
  fun testRetryTwoTimes() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emmiter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)

    listing.retry.invoke()
    assertEquals(NetworkState.LOADING, listing.networkState.value)
    mockedNetworkDataSourceAdapter.sendPageResponse(1)
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    listing.retry.invoke()
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
