package com.xmartlabs.fountain.pagefetcher.totalpages

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.pagefetcher.NetworkDataSourceWithTotalPageCountAdapter
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourcePageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.getPagedListSize
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import com.xmartlabs.fountain.testutils.extensions.sendListResponseWithPageCountResponse
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

    pageFetcher.sendListResponseWithPageCountResponse(1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithPageCountResponse(1, 1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())
    listing.scrollToTheEnd()
  }

  @Test
  fun testFetchTwoPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>()
    val listing = createListing(NetworkDataSourceWithTotalPageCountAdapter(pageFetcher))
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithPageCountResponse(2)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateSpecificIntPageResponseList(0), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithPageCountResponse(2, 1)
    Assert.assertEquals(2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateSpecificIntPageResponseList(0, 1), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithPageCountResponse(2, 1)
    Assert.assertEquals(2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateSpecificIntPageResponseList(0, 1), listing.getPagedList())
    listing.scrollToTheEnd()
  }

  abstract fun createListing(mockedNetworkDataSourceAdapter: NetworkDataSourceAdapter<out ListResponse<Int>>)
      : Listing<Int>
}
