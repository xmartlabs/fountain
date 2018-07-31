package com.xmartlabs.fountain.pagefetcher.totalpages

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkDataSourceWithTotalPageCountAdapter
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourcePageFetcher
import com.xmartlabs.fountain.common.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.common.extensions.getPagedList
import com.xmartlabs.fountain.common.extensions.getPagedListSize
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendListResponseWithPageCountResponseIfIsRequired
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class NetworkDataSourceWithTotalPageCountAdapterUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testFetchOnePage() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>()
    val listing = createListing(NetworkDataSourceWithTotalPageCountAdapter(pageFetcher))
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithPageCountResponseIfIsRequired(1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithPageCountResponseIfIsRequired(1, 1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }

  @Test
  fun testFetchTwoPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>()
    val listing = createListing(NetworkDataSourceWithTotalPageCountAdapter(pageFetcher))
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithPageCountResponseIfIsRequired(2)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithPageCountResponseIfIsRequired(2, 1)
    Assert.assertEquals(2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())

    pageFetcher.sendListResponseWithPageCountResponseIfIsRequired(2, 1)
    Assert.assertEquals(2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())
  }

  abstract fun createListing(mockedNetworkDataSourceAdapter: NetworkDataSourceAdapter<out ListResponse<Int>>)
      : Listing<Int>
}
