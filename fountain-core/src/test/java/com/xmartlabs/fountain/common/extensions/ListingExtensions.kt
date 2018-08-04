package com.xmartlabs.fountain.common.extensions

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
  lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
}

fun <T> Listing<T>.getPagedListSize() = pagedList.value?.size?.or(0)

fun <T> Listing<T>.getPagedList() = pagedList.value!!.getList()
