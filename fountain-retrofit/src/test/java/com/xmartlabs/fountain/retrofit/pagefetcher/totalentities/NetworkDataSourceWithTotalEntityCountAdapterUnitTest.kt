package com.xmartlabs.fountain.retrofit.pagefetcher.totalentities

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.retrofit.adapter.NetworkDataSourceAdapterFactory
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.common.IntMockedListingCreator
import com.xmartlabs.fountain.retrofit.common.toRxPageFetcher
import com.xmartlabs.fountain.testutils.MockedNetworkDataSourcePageFetcher
import com.xmartlabs.fountain.testutils.extensions.TestConstants
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.getPagedListSize
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
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
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(pageFetcher.toRxPageFetcher())
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    pageFetcher.sendListResponseWithEntityCountResponse(TestConstants.DEFAULT_NETWORK_PAGE_SIZE.toLong())
    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithEntityCountResponse(TestConstants.DEFAULT_NETWORK_PAGE_SIZE.toLong(), 1)
    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())
  }

  @Test
  fun testFetchTwoPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>()
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(pageFetcher.toRxPageFetcher())
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    val entityCount = 2 * TestConstants.DEFAULT_NETWORK_PAGE_SIZE
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong())
    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 1)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 2)
    IntMockedListingCreator.IO_EXCECUTOR.awaitTermination()
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())
  }

  @Test
  fun testFetchTwoAndAHalfPages() {
    val pageFetcher = MockedNetworkDataSourcePageFetcher<ListResponseWithEntityCount<Int>>()
    val mockedNetworkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(pageFetcher.toRxPageFetcher())
    val listing = createListing(mockedNetworkDataSourceAdapter)
        .mockLifecycleEvents()

    val entityCount = 5 / 2 * TestConstants.DEFAULT_NETWORK_PAGE_SIZE
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong())
    Assert.assertEquals(TestConstants.DEFAULT_NETWORK_PAGE_SIZE, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0), listing.getPagedList())

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 1)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals(generateIntPageResponseList(0, 1), listing.getPagedList())

    val start = TestConstants.DEFAULT_NETWORK_PAGE_SIZE * 2
    val response =  (start..(entityCount - 1)).toList()
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), response)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals((0 until entityCount).toList(), listing.getPagedList())

    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), 6)
    pageFetcher.sendListResponseWithEntityCountResponse(entityCount.toLong(), response)
    Assert.assertEquals(entityCount, listing.getPagedListSize())
    Assert.assertEquals((0 until entityCount).toList(), listing.getPagedList())
  }

  abstract fun createListing(mockedNetworkDataSourceAdapter: RetrofitNetworkDataSourceAdapter<out ListResponse<Int>>)
      : Listing<Int>
}
