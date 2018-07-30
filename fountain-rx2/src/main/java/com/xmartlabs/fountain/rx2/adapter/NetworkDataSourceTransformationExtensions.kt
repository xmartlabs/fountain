package com.xmartlabs.fountain.rx2.adapter

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import com.xmartlabs.fountain.rx2.common.observeOn
import com.xmartlabs.fountain.rx2.common.subscribeOn
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor

private fun <T> NetworkResultListener<T>.toSingleObserver(): SingleObserver<T> = object : SingleObserver<T> {
  override fun onSuccess(t: T) = this@toSingleObserver.onSuccess(t)

  override fun onSubscribe(d: Disposable) {}

  override fun onError(e: Throwable) = this@toSingleObserver.onError(e)
}

internal fun <T> RxPageFetcher<T>.toPageFetcher(ioServiceExecutor: Executor, ioDatabaseExecutor: Executor)
    : PageFetcher<T> {
  return object : PageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      this@toPageFetcher.fetchPage(page = page, pageSize = pageSize)
          .subscribeOn(ioServiceExecutor)
          .observeOn(ioDatabaseExecutor)
          .subscribe(networkResultListener.toSingleObserver())
    }
  }
}

internal fun <T> RxPageFetcher<T>.toPageFetcher()
    : PageFetcher<T> {
  return object : PageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      this@toPageFetcher.fetchPage(page = page, pageSize = pageSize)
          .subscribe(networkResultListener.toSingleObserver())
    }
  }
}

internal fun <T> RxNetworkDataSourceAdapter<T>.toNetworkDataSourceAdapter(
    ioServiceExecutor: Executor, ioDatabaseExecutor: Executor)
    : NetworkDataSourceAdapter<T> {
  return object : NetworkDataSourceAdapter<T> {
    override val pageFetcher = this@toNetworkDataSourceAdapter.toPageFetcher(
        ioServiceExecutor = ioServiceExecutor,
        ioDatabaseExecutor = ioDatabaseExecutor)

    override fun canFetch(page: Int, pageSize: Int): Boolean =
        this@toNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
  }
}


