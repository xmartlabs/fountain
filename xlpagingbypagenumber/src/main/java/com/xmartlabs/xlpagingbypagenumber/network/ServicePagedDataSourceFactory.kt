package com.xmartlabs.xlpagingbypagenumber.network

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.xmartlabs.xlpagingbypagenumber.common.ListResponsePageFetcher
import java.util.concurrent.Executor

internal class ServicePagedDataSourceFactory<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pageFetcher: ListResponsePageFetcher<T>
) : DataSource.Factory<Int, T>() {
  val sourceLiveData = MutableLiveData<PagedDataSource<T>>()

  override fun create(): DataSource<Int, T> {
    val source = PagedDataSource(
        pageFetcher = pageFetcher,
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor
    )
    sourceLiveData.postValue(source)
    return source
  }
}
