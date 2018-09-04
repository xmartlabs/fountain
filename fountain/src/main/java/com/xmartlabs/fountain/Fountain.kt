package com.xmartlabs.fountain

import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.common.IoExecutors
import com.xmartlabs.fountain.feature.cachednetwork.CachedNetworkListingCreator
import com.xmartlabs.fountain.feature.network.NetworkPagedListingCreator
import java.util.concurrent.Executor

/** A [Listing] factory */
object Fountain {
  private const val DEFAULT_FIRST_PAGE = 1
  private const val DEFAULT_NETWORK_PAGE_SIZE = 20
  private val DEFAULT_PAGED_LIST_CONFIG = PagedList.Config.Builder()
      .setPageSize(DEFAULT_NETWORK_PAGE_SIZE)
      .build()

  /**
   * Creates a [Listing] with Network support.
   *
   * @param NetworkValue The listed entity type.
   * @param networkDataSourceAdapter The [NetworkDataSourceAdapter] to manage the paged service endpoint.
   * The default value is 1.
   * @param firstPage The first page number, defined by the service.
   * @param ioServiceExecutor The [Executor] with which the service call will be made.
   * By default, it is a pool of 5 threads.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, ServiceResponse : ListResponse<NetworkValue>> createNetworkListing(
      networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>,
      firstPage: Int = DEFAULT_FIRST_PAGE,
      ioServiceExecutor: Executor = IoExecutors.NETWORK_EXECUTOR,
      pagedListConfig: PagedList.Config = DEFAULT_PAGED_LIST_CONFIG
  ) = NetworkPagedListingCreator.createListing(
      firstPage = firstPage,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter
  )

  /**
   * Creates a [Listing] with Cache + Network Support.
   *
   * @param NetworkValue The network entity type.
   * @param DataSourceValue The [DataSource] entity type.
   * @param networkDataSourceAdapter The [NetworkDataSourceAdapter] to manage the paged service endpoint.
   * @param cachedDataSourceAdapter The [CachedDataSourceAdapter] to take control of the [DataSource].
   * The default value is 1.
   * @param firstPage The first page number, defined by the service.
   * @param ioServiceExecutor The [Executor] with which the service call will be made.
   * By default, it is a pool of 5 threads.
   * @param ioDatabaseExecutor The [Executor] through which the database transactions will be made.
   * By default the library will use a single thread executor.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Cache + Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, DataSourceValue, ServiceResponse : ListResponse<NetworkValue>> createNetworkWithCacheSupportListing(
      networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>,
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      ioServiceExecutor: Executor = IoExecutors.NETWORK_EXECUTOR,
      ioDatabaseExecutor: Executor = IoExecutors.DATABASE_EXECUTOR,
      firstPage: Int = DEFAULT_FIRST_PAGE,
      pagedListConfig: PagedList.Config = DEFAULT_PAGED_LIST_CONFIG
  ) = CachedNetworkListingCreator.createListing(
      cachedDataSourceAdapter = cachedDataSourceAdapter,
      firstPage = firstPage,
      ioDatabaseExecutor = ioDatabaseExecutor,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter
  )
}
