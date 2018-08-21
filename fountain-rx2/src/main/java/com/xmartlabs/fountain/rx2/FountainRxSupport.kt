package com.xmartlabs.fountain.rx2

import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.feature.cachednetwork.CachedNetworkListingCreator
import com.xmartlabs.fountain.feature.network.NetworkPagedListingCreator
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.adapter.toNetworkDataSourceAdapter
import java.util.concurrent.Executor

/** A [Listing] factory */
object FountainRxSupport {
  /**
   * Creates a [Listing] with Network support.
   *
   * @param NetworkValue The listed entity type.
   * @param networkDataSourceAdapter The [RxNetworkDataSourceAdapter] to manage the paged service endpoint.
   * @param firstPage The first page number, defined by the service.
   * The default value is 1.
   * @param ioServiceExecutor The [Executor] with which the service call will be made.
   * By default, it is a pool of 5 threads.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, ServiceResponse : ListResponse<NetworkValue>> createNetworkListing(
      networkDataSourceAdapter: RxNetworkDataSourceAdapter<ServiceResponse>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE,
      ioServiceExecutor: Executor = FountainConstants.NETWORK_EXECUTOR,
      pagedListConfig: PagedList.Config = FountainConstants.DEFAULT_PAGED_LIST_CONFIG
  ) = NetworkPagedListingCreator.createListing(
      firstPage = firstPage,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter.toNetworkDataSourceAdapter()
  )

  /**
   * Creates a [Listing] with Cache + Network Support.
   *
   * @param NetworkValue The network entity type.
   * @param DataSourceValue The [DataSource] entity type.
   * @param networkDataSourceAdapter The [RxNetworkDataSourceAdapter] to manage the paged service endpoint.
   * @param cachedDataSourceAdapter The [CachedDataSourceAdapter] to take control of the [DataSource].
   * @param firstPage The first page number, defined by the service.
   * The default value is 1.
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
  fun <NetworkValue, DataSourceValue> createNetworkWithCacheSupportListing(
      networkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<out NetworkValue>>,
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      ioServiceExecutor: Executor = FountainConstants.NETWORK_EXECUTOR,
      ioDatabaseExecutor: Executor = FountainConstants.DATABASE_EXECUTOR,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig: PagedList.Config = FountainConstants.DEFAULT_PAGED_LIST_CONFIG
  ) = CachedNetworkListingCreator.createListing(
      cachedDataSourceAdapter = cachedDataSourceAdapter,
      firstPage = firstPage,
      ioDatabaseExecutor = ioDatabaseExecutor,
      ioServiceExecutor = ioServiceExecutor,
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter.toNetworkDataSourceAdapter()
  )
}
