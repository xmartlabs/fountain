package com.xmartlabs.fountain.common

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import io.reactivex.Single
import io.reactivex.SingleEmitter

class MockedNetworkDataSourceAdapter<T> : NetworkDataSourceAdapter<T> {
  var emmiter: SingleEmitter<T>? = null

  override fun fetchPage(page: Int, pageSize: Int): Single<out T> {
    return Single.create { emitter ->
      this.emmiter = emitter
    }
  }

  override fun canFetch(page: Int, pageSize: Int): Boolean = true
}
