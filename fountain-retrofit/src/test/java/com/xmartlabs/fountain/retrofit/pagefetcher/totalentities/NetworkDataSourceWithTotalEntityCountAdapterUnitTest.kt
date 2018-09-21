package com.xmartlabs.fountain.retrofit.pagefetcher.totalentities

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.retrofit.adapter.NetworkDataSourceAdapterFactory
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.common.EntityCountMockedPageFetcher
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

abstract class NetworkDataSourceWithTotalEntityCountAdapterUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testFetchOnePage() {
    val pageFetcher = EntityCountMockedPageFetcher(TestConstants.DEFAULT_NETWORK_PAGE_SIZE.toLong())
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(pageFetcher)
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
    val entityCount = 2 * TestConstants.DEFAULT_NETWORK_PAGE_SIZE
    val pageFetcher = EntityCountMockedPageFetcher(entityCount.toLong())
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(pageFetcher)
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
  }

  @Test
  fun testFetchTwoAndAHalfPages() {
    val entityCount =  (5f / 2f * TestConstants.DEFAULT_NETWORK_PAGE_SIZE).toInt()
    val pageFetcher = EntityCountMockedPageFetcher(entityCount.toLong())
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(pageFetcher)
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()


    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE * 2, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals((0 until entityCount).toList(), listing.getPagedList())
    listing.scrollToTheEnd()

    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals((0 until entityCount).toList(), listing.getPagedList())
  }

  abstract fun createListing(mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>)
      : Listing<Int>
}
