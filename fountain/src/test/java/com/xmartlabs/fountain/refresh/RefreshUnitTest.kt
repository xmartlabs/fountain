package com.xmartlabs.fountain.refresh

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.Fountain
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class RefreshUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testRefreshContent() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    assertEquals(Fountain.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    listing.refresh.invoke()
    mockedNetworkDataSourceAdapter.sendPageResponse(page = 1)

    assertEquals(Fountain.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
