package com.xmartlabs.xlpagingbypagenumber.network

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.xmartlabs.xlpagingbypagenumber.fetcher.ListResponsePagingHandler
import java.util.concurrent.Executor

internal class ServicePagedDataSourceFactory<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagingHandler: ListResponsePagingHandler<T>
) : DataSource.Factory<Int, T>() {
  val sourceLiveData = MutableLiveData<PagedDataSource<T>>()

  override fun create(): DataSource<Int, T> {
    val source = PagedDataSource(
        pagingHandler = pagingHandler,
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor
    )
    sourceLiveData.postValue(source)
    return source
  }
}
