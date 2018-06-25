package com.xmartlabs.fountain.networkstate

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import com.xmartlabs.fountain.Fountain
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.common.InstantExecutor
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock

abstract class NetworkStatusUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testLoadingOnePage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = getMockedListing(mockedNetworkDataSourceAdapter)

    assertEquals(NetworkState.LOADING, listing.networkState.value)
  }

  @Test
  fun testLoadingTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = getMockedListing(mockedNetworkDataSourceAdapter)
    assertEquals(NetworkState.LOADING, listing.networkState.value)

    sendPageResponse(mockedNetworkDataSourceAdapter)
    assertEquals(NetworkState.LOADED, listing.networkState.value)
    listing.pagedList.value!!.loadAround(Fountain.DEFAULT_NETWORK_PAGE_SIZE - 1)

    assertEquals(NetworkState.LOADING, listing.networkState.value)
  }

  @Test
  fun testSuccessOnePage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = getMockedListing(mockedNetworkDataSourceAdapter)

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    sendPageResponse(mockedNetworkDataSourceAdapter)
    assertEquals(NetworkState.LOADED, listing.networkState.value)
  }

  @Test
  fun testSuccessTwoPages() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = getMockedListing(mockedNetworkDataSourceAdapter)

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    sendPageResponse(mockedNetworkDataSourceAdapter)
    assertEquals(NetworkState.LOADED, listing.networkState.value)

    listing.pagedList.value!!.loadAround(Fountain.DEFAULT_NETWORK_PAGE_SIZE - 1)
    assertEquals(NetworkState.LOADING, listing.networkState.value)

    sendPageResponse(mockedNetworkDataSourceAdapter)
    assertEquals(NetworkState.LOADED, listing.networkState.value)
  }

  @Test
  fun testErrorFirstPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = getMockedListing(mockedNetworkDataSourceAdapter)

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emmiter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)
  }

  @Test
  fun testErrorInSecondPage() {
    val mockedNetworkDataSourceAdapter = MockedNetworkDataSourceAdapter<ListResponse<Int>>()
    val listing = getMockedListing(mockedNetworkDataSourceAdapter)

    assertEquals(NetworkState.LOADING, listing.networkState.value)

    val exception = Exception()
    mockedNetworkDataSourceAdapter.emmiter?.onError(exception)
    assertEquals(NetworkState.error(exception), listing.networkState.value)
  }

  private fun getMockedListing(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>): Listing<Int> {
    val listing = createListing(mockedNetworkDataSourceAdapter)
    val lifecycle = LifecycleRegistry(mock(LifecycleOwner::class.java))
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

    listing.pagedList.observe({ lifecycle }) { }
    listing.networkState.observe({ lifecycle }) { }
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    return listing
  }

  protected abstract fun createListing(
      mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>
  ): Listing<Int>

  private fun sendPageResponse(mockedNetworkDataSourceAdapter: MockedNetworkDataSourceAdapter<ListResponse<Int>>) {
    mockedNetworkDataSourceAdapter.emmiter?.onSuccess(object : ListResponse<Int> {
      override fun getElements(): List<Int> = (0..Fountain.DEFAULT_NETWORK_PAGE_SIZE).toList()
    })
  }
}
