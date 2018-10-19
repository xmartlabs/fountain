package com.xmartlabs.fountain.coroutines.pagefetcher.notpaged

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.coroutines.adapter.NotPagedCoroutinePageFetcher
import com.xmartlabs.fountain.coroutines.common.NotPagedMockedPageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.getPagedList
import com.xmartlabs.fountain.testutils.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.testutils.extensions.scrollToTheEnd
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

abstract class NotPagedUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testSimpleCall() {
    val pageFetcher = NotPagedMockedPageFetcher(false)
    val listing = createListing(pageFetcher)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  @Test
  fun testSimpleCallWithScrolling() {
    val pageFetcher = NotPagedMockedPageFetcher(false)
    val listing = createListing(pageFetcher)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())

    listing.scrollToTheEnd()
    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  @Test
  fun testRetryFirstCall() {
    val pageFetcher = NotPagedMockedPageFetcher(true)
    val listing = createListing(pageFetcher)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Error)

    pageFetcher.error = false
    listing.retry.invoke()
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())

    listing.scrollToTheEnd()
    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())
  }

  @Test
  fun testRefreshSecondFirstCall() {
    val pageFetcher = NotPagedMockedPageFetcher(false)
    val listing = createListing(pageFetcher)
        .mockLifecycleEvents()

    assert(listing.networkState.value is NetworkState.Loaded)
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())

    listing.scrollToTheEnd()

    assert(listing.networkState.value is NetworkState.Loaded)

    listing.refresh.invoke()
    assertEquals(generateIntPageResponseList(1), listing.getPagedList())
    assert(listing.networkState.value is NetworkState.Loaded)
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: NotPagedCoroutinePageFetcher<ListResponse<Int>>
  ): Listing<Int>
}
