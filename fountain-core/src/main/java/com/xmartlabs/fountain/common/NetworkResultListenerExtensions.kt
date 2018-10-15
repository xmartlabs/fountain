package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.adapter.NetworkResultListener

// It's not an inline method because Jacoco has some issues with that
// https://issuetracker.google.com/u/1/issues/109771903
fun <T> NetworkResultListener<T>.notifyFromCallable(callable: () -> T) {
  @Suppress("TooGenericExceptionCaught")
  try {
    onSuccess(callable.invoke())
  } catch (throwable: Throwable) {
    onError(throwable)
  }
}
