package com.xmartlabs.fountain.adapter

import android.support.annotation.NonNull

interface NetworkResultListener<T> {
  fun onSuccess(@NonNull response: T)

  fun onError(@NonNull t: Throwable)
}

interface NetworkRequester<T> {
  fun excecute(listener: NetworkResultListener<T>)
}