package com.xmartlabs.xlpagingbypagenumber.network

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.xmartlabs.xlpagingbypagenumber.common.ListResponsePageFetcher
import java.util.concurrent.Executor

internal class ServicePagedDataSourceFactory<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagedListConfig: PagedList.Config,
    private val pageFetcher: ListResponsePageFetcher<T>
) : DataSource.Factory<Int, T>() {
  val sourceLiveData = MutableLiveData<PagedDataSource<T>>()

  override fun create(): DataSource<Int, T> {
    val source = PagedDataSource(
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor,
        pagedListConfig = pagedListConfig,
        pageFetcher = pageFetcher
    )
    sourceLiveData.postValue(source)
    return source
  }
}
