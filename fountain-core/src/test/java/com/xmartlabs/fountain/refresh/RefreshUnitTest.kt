package com.xmartlabs.fountain.refresh

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.TestConstants
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.getPagedListSize
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.sendPageResponse
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

    assert(listing.networkState.value is NetworkState.Loading)

    mockedNetworkDataSourceAdapter.sendPageResponse()
    assert(listing.networkState.value is NetworkState.Loaded)

    assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())

    listing.refresh.invoke()
    mockedNetworkDataSourceAdapter.sendPageResponse(page = 1)

    assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    assertEquals(generateSpecificIntPageResponseList(1), listing.getPagedList())
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
