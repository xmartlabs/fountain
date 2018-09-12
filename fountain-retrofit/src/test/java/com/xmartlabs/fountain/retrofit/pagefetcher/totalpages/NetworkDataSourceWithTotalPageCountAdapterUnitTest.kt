package com.xmartlabs.fountain.retrofit.pagefetcher.totalpages

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.retrofit.adapter.NetworkDataSourceAdapterFactory
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.common.IntMockedListingCreator
import com.xmartlabs.fountain.retrofit.common.toRxPageFetcher
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourcePageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.getPagedListSize
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
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
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(pageFetcher.toRxPageFetcher())
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithPageCountResponse(1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithPageCountResponse(1, 1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }

  @Test
  fun testFetchTwoPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithPageCount<Int>>()
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(pageFetcher.toRxPageFetcher())
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithPageCountResponse(2)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithPageCountResponse(2, 1)
    Assert.assertEquals(2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())

    pageFetcher.sendListResponseWithPageCountResponse(2, 1)
    Assert.assertEquals(2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())
  }

  abstract fun createListing(mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>)
      : Listing<Int>
}
