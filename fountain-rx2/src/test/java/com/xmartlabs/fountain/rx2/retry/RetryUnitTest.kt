package com.xmartlabs.fountain.rx2.retry

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.common.MockedPageFetcher
import com.xmartlabs.fountain.rx2.common.toInfiniteRxNetworkDataSourceAdapter
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class RetryUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testRetryFirstCall() {
    val pageFetcher = MockedPageFetcher(true)
    val listing = createListing(pageFetcher.toInfiniteRxNetworkDataSourceAdapter())
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Error)

    pageFetcher.error = false
    listing.retry.invoke()
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  @Test
  fun testRetrySecondFirstCall() {
    val pageFetcher = MockedPageFetcher()
    val listing = createListing(pageFetcher.toInfiniteRxNetworkDataSourceAdapter())
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())

    pageFetcher.error = true
    listing.scrollToTheEnd()

    assert(listing.networkState.value is NetworkState.Error)

    pageFetcher.error = false
    listing.retry.invoke()
    assertEquals(generateIntPageResponseList(2), listing.getPagedList())
  }


  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: RxNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>
}
