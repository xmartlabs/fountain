package com.xmartlabs.fountain.pagefetcher.totalentities

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.pagefetcher.NetworkDataSourceWithTotalEntityCountAdapter
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourcePageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.getPagedListSize
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import com.xmartlabs.fountain.testutils.extensions.sendListResponseWithEntityCountResponse
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class NetworkDataSourceWithTotalEntityCountAdapterUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testFetchOnePage() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>()
    val listing = createListing(NetworkDataSourceWithTotalEntityCountAdapter(pageFetcher))
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithEntityCountResponse(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE.toLong())
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithEntityCountResponse(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE.toLong(), 1)
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  @Test
  fun testFetchTwoPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>()
    val listing = createListing(NetworkDataSourceWithTotalEntityCountAdapter(pageFetcher))
        .mockLifecycleEvents()

    val entityCount = 2 * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong())
    Assert.assertEquals(IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 1)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 2)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
    listing.scrollToTheEnd()
  }

  @Test
  fun testFetchTwoAndAHalfPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>()
    val listing = createListing(NetworkDataSourceWithTotalEntityCountAdapter(pageFetcher)).mockLifecycleEvents()
    val entityCount: Int = (5f / 2f * IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE).toInt()
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong())
    Assert.assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 1)
    Assert.assertEquals(generateIntPageResponseList(2), listing.getPagedList())
    listing.scrollToTheEnd()

    val start = IntMockedListingCreator.DEFAULT_NETWORK_PAGE_SIZE * 2
    val response = (start..(entityCount - 1)).toList()
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), response)
    Assert.assertEquals((0 until entityCount).toList(), listing.getPagedList())
    listing.scrollToTheEnd()

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 6)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals((0 until entityCount).toList(), listing.getPagedList())
  }

  abstract fun createListing(
      mockedNetworkDataSourceAdapter: BaseNetworkDataSourceAdapter<out ListResponse<Int>>
  ): Listing<Int>
}
