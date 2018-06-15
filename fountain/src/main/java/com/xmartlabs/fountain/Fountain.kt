package com.xmartlabs.fountain

import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.xmartlabs.fountain.common.IoExecutors
import com.xmartlabs.fountain.feature.cachednetwork.CachedNetworkListingCreator
import com.xmartlabs.fountain.feature.cachednetwork.DataSourceEntityHandler
import com.xmartlabs.fountain.fetcher.ListResponsePagingHandler
import com.xmartlabs.fountain.fetcher.PagingHandler
import com.xmartlabs.sample.repository.common.NetworkPagedListingCreator
import java.util.concurrent.Executor

object Fountain {
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
  ) = CachedNetworkListingCreator.createListing(
      dataSourceEntityHandler = dataSourceEntityHandler,
      dataSourceFactory = dataSourceFactory,
      firstPage = firstPage,
      ioDatabaseExecutor = ioDatabaseExecutor,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      pagingHandler = pagingHandler
  )
}
