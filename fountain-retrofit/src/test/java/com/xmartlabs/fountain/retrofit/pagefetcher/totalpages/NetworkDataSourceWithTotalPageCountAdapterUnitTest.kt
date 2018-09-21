package com.xmartlabs.fountain.retrofit.pagefetcher.totalpages

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.retrofit.adapter.NetworkDataSourceAdapterFactory
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.common.PageCountMockedPageFetcher
import com.xmartlabs.fountain.testutils.TestConstants
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.getPagedListSize
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class NetworkDataSourceWithTotalPageCountAdapterUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testFetchOnePage() {
    val pageFetcher = PageCountMockedPageFetcher(1)
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(pageFetcher)
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  @Test
  fun testFetchTwoPages() {
    val pageFetcher = PageCountMockedPageFetcher(2)
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalPageCountListResponse(pageFetcher)
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(2 * TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(2 * TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
  }

  abstract fun createListing(mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>)
      : Listing<Int>
}
