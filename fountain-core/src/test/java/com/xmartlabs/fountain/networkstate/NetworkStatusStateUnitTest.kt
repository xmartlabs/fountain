package com.xmartlabs.fountain.networkstate

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.Status
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
    assertEquals(errorState.throwable, exception)
  }

  @Test
  fun testSuccessStatus() {
    assertEquals(Status.SUCCESS, NetworkState.LOADED.status)
  }
}
