package com.xmartlabs.fountain.networkstate

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.Status
import com.xmartlabs.fountain.common.IntMockedListingCreator
import com.xmartlabs.fountain.common.MockedNetworkDataSourceAdapter
import com.xmartlabs.fountain.common.extensions.mockLifecycleEvents
import com.xmartlabs.fountain.common.extensions.sendPageResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class NetworkStatusStateUnitTest {
  @get:Rule
  var rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun testRunningStatus() {
    assertEquals(Status.RUNNING, NetworkState.LOADING.status)
  }

  @Test
  fun testFailedStatus() {
    val exceptionMessage = "Test Message"
    val exception = Exception(exceptionMessage)
    val errorState = NetworkState.error(exception)
    assertEquals(Status.FAILED, errorState.status)
    assertEquals(exceptionMessage, errorState.msg)
    assertEquals(errorState.throwable, exception)
  }

  @Test
  fun testSuccessStatus() {
    assertEquals(Status.SUCCESS, NetworkState.LOADED.status)
  }
}
