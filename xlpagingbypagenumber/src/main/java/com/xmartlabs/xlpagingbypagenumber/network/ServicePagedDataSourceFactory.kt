package com.xmartlabs.xlpagingbypagenumber.network

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.xmartlabs.xlpagingbypagenumber.fetcher.ListResponsePagingHandler
import java.util.concurrent.Executor

internal class ServicePagedDataSourceFactory<T>(
    private val firstPage: Int,
    private val ioServiceExecutor: Executor,
    private val pagedListConfig: PagedList.Config,
    private val pagingHandler: ListResponsePagingHandler<T>
) : DataSource.Factory<Int, T>() {
  val sourceLiveData = MutableLiveData<PagedDataSource<T>>()

  override fun create(): DataSource<Int, T> {
    val source = PagedDataSource(
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor,
        pagedListConfig = pagedListConfig,
        pagingHandler = pagingHandler
    )
    sourceLiveData.postValue(source)
    return source
  }
}
