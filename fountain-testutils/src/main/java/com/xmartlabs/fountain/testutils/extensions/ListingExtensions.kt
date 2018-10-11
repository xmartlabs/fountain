package com.xmartlabs.fountain.testutils.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import com.xmartlabs.fountain.Listing
import org.mockito.Mockito

fun <T> Listing<T>.mockLifecycleEvents(): Listing<T> = apply {
  val lifecycle = LifecycleRegistry(Mockito.mock(LifecycleOwner::class.java))

  lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  refreshState.observe({ lifecycle }) { }
  pagedList.observe({ lifecycle }) { }
  networkState.observe({ lifecycle }) { }
}

fun <T> Listing<T>.getPagedListSize() = pagedList.value?.size?.or(0)

@Suppress("UnsafeCallOnNullableType")
fun <T> Listing<T>.getPagedList() = pagedList.value!!.getList()

@Suppress("UnsafeCallOnNullableType")
fun Listing<*>.scrollToTheEnd() = pagedList.value!!.scrollToTheEnd()
