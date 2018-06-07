package com.xmartlabs.xlpagingbypagenumber

import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.xmartlabs.template.repository.common.NetworkPagedListingCreator
import com.xmartlabs.xlpagingbypagenumber.common.IoExecutors
import com.xmartlabs.xlpagingbypagenumber.feature.cachednetwork.DataSourceEntityHandler
import com.xmartlabs.xlpagingbypagenumber.feature.cachednetwork.CacheNetworkListingCreator
import com.xmartlabs.xlpagingbypagenumber.fetcher.ListResponsePagingHandler
import com.xmartlabs.xlpagingbypagenumber.fetcher.PagingHandler
import java.util.concurrent.Executor

object XlPaging {
  private const val DEFAULT_FIRST_PAGE = 1
  private const val DEFAULT_NETWORK_PAGE_SIZE = 20
  private val DEFAULT_PAGED_LIST_CONFIG = PagedList.Config.Builder()
      .setPageSize(DEFAULT_NETWORK_PAGE_SIZE)
      .build()

  fun <Value> createNetworkListing(
      firstPage: Int = DEFAULT_FIRST_PAGE,
      ioServiceExecutor: Executor = IoExecutors.NETWORK_EXECUTOR,
      pagedListConfig: PagedList.Config = DEFAULT_PAGED_LIST_CONFIG,
      pagingHandler: ListResponsePagingHandler<Value>
  ) = NetworkPagedListingCreator.createListing(
      firstPage = firstPage,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      pagingHandler = pagingHandler
  )

  fun <Value, ServiceResponse> createNetworkWithCacheSupportListing(
      dataSourceFactory: DataSource.Factory<*, Value>,
      pagingHandler: PagingHandler<out ServiceResponse>,
      dataSourceEntityHandler: DataSourceEntityHandler<ServiceResponse>,
      ioServiceExecutor: Executor = IoExecutors.NETWORK_EXECUTOR,
      ioDatabaseExecutor: Executor = IoExecutors.DATABASE_EXECUTOR,
      firstPage: Int = DEFAULT_FIRST_PAGE,
      pagedListConfig: PagedList.Config = DEFAULT_PAGED_LIST_CONFIG
  ) = CacheNetworkListingCreator.createListing(
      dataSourceEntityHandler = dataSourceEntityHandler,
      dataSourceFactory = dataSourceFactory,
      firstPage = firstPage,
      ioDatabaseExecutor = ioDatabaseExecutor,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      pagingHandler = pagingHandler
  )
}
