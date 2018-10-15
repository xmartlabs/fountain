package com.xmartlabs.fountain.coroutines

import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.common.Experimental
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.adapter.NotPagedCoroutinePageFetcher
import com.xmartlabs.fountain.coroutines.adapter.toBaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.common.toExecutor
import com.xmartlabs.fountain.feature.cachednetwork.CachedNetworkListingCreator
import com.xmartlabs.fountain.feature.network.NetworkPagedListingCreator
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.asCoroutineDispatcher

/** A [Listing] factory */
object FountainCoroutines {
  /**
   * Creates a [Listing] with Network support.
   *
   * @param NetworkValue The listed entity type.
   * @param networkDataSourceAdapter The [CoroutineNetworkDataSourceAdapter] to manage the paged service endpoint.
   * @param firstPage The first page number, defined by the service.
   * The default value is 1.
   * @param ioServiceCoroutineDispatcher The [Dispatchers] with which the service call will be made.
   * @param coroutineScope The [CoroutineScope] where the couroutine will be executed.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue> createNetworkListing(
      networkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<out NetworkValue>>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE,
      ioServiceCoroutineDispatcher: CoroutineDispatcher = FountainConstants.NETWORK_EXECUTOR.asCoroutineDispatcher(),
      coroutineScope: CoroutineScope = GlobalScope,
      pagedListConfig: PagedList.Config = FountainConstants.DEFAULT_PAGED_LIST_CONFIG
  ) = NetworkPagedListingCreator.createListing(
      firstPage = firstPage,
      ioServiceExecutor = ioServiceCoroutineDispatcher.toExecutor(coroutineScope),
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter.toBaseNetworkDataSourceAdapter()
  )

  /**
   * Creates a [Listing] with Network support from a not paged endpoint.
   *
   * @param NetworkValue The listed entity type.
   * @param notPagedCoroutinePageFetcher The [NotPagedCoroutinePageFetcher] that is used to perform the service requests.
   * @param ioServiceCoroutineDispatcher The [Dispatchers] with which the service call will be made.
   * @param coroutineScope The [CoroutineScope] where the couroutine will be executed.
   * @return A [Listing] structure with Network Support.
   */
  @Experimental
  fun <NetworkValue> createNotPagedNetworkListing(
      notPagedCoroutinePageFetcher: NotPagedCoroutinePageFetcher<out ListResponse<out NetworkValue>>,
      ioServiceCoroutineDispatcher: CoroutineDispatcher = FountainConstants.NETWORK_EXECUTOR.asCoroutineDispatcher(),
      coroutineScope: CoroutineScope = GlobalScope
  ) = NetworkPagedListingCreator.createListing(
      firstPage = FountainConstants.DEFAULT_FIRST_PAGE,
      ioServiceExecutor = ioServiceCoroutineDispatcher.toExecutor(coroutineScope),
      pagedListConfig = FountainConstants.DEFAULT_PAGED_LIST_CONFIG,
      networkDataSourceAdapter = notPagedCoroutinePageFetcher.toBaseNetworkDataSourceAdapter()
  )

  /**
   * Creates a [Listing] with Cache + Network Support.
   *
   * @param NetworkValue The network entity type.
   * @param DataSourceValue The [DataSource] entity type.
   * @param networkDataSourceAdapter The [CoroutineNetworkDataSourceAdapter] to manage the paged service endpoint.
   * @param cachedDataSourceAdapter The [CachedDataSourceAdapter] to take control of the [DataSource].
   * @param firstPage The first page number, defined by the service.
   * The default value is 1.
   * @param ioServiceCoroutineDispatcher The [Dispatchers] with which the service call will be made.
   * @param ioDatabaseCoroutineDispatcher The [Dispatchers] through which the database transactions will be made.
   * By default the library will use a single thread executor.
   * @param coroutineScope The [CoroutineScope] where the couroutine will be executed.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Cache + Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, DataSourceValue> createNetworkWithCacheSupportListing(
      networkDataSourceAdapter: CoroutineNetworkDataSourceAdapter<out ListResponse<out NetworkValue>>,
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      ioServiceCoroutineDispatcher: CoroutineDispatcher = FountainConstants.NETWORK_EXECUTOR.asCoroutineDispatcher(),
      ioDatabaseCoroutineDispatcher: CoroutineDispatcher = FountainConstants.DATABASE_EXECUTOR.asCoroutineDispatcher(),
      coroutineScope: CoroutineScope = GlobalScope,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig: PagedList.Config = FountainConstants.DEFAULT_PAGED_LIST_CONFIG
  ) = CachedNetworkListingCreator.createListing(
      cachedDataSourceAdapter = cachedDataSourceAdapter,
      firstPage = firstPage,
      ioDatabaseExecutor = ioDatabaseCoroutineDispatcher.toExecutor(coroutineScope),
      ioServiceExecutor = ioServiceCoroutineDispatcher.toExecutor(coroutineScope),
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter.toBaseNetworkDataSourceAdapter()
  )

  /**
   * Creates a [Listing] with Cache + Network Support from a not paged endpoint.
   *
   * @param NetworkValue The network entity type.
   * @param DataSourceValue The [DataSource] entity type.
   * @param notPagedCoroutinePageFetcher The [NotPagedCoroutinePageFetcher] that is used to perform the service requests.
   * @param cachedDataSourceAdapter The [CachedDataSourceAdapter] to take control of the [DataSource].
   * @param ioServiceCoroutineDispatcher The [Dispatchers] with which the service call will be made.
   * @param ioDatabaseCoroutineDispatcher The [Dispatchers] through which the database transactions will be made.
   * By default the library will use a single thread executor.
   * @param coroutineScope The [CoroutineScope] where the couroutine will be executed.
   * @return A [Listing] structure with Cache + Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, DataSourceValue> createNotPagedNetworkWithCacheSupportListing(
      notPagedCoroutinePageFetcher: NotPagedCoroutinePageFetcher<out ListResponse<out NetworkValue>>,
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      ioServiceCoroutineDispatcher: CoroutineDispatcher = FountainConstants.NETWORK_EXECUTOR.asCoroutineDispatcher(),
      ioDatabaseCoroutineDispatcher: CoroutineDispatcher = FountainConstants.DATABASE_EXECUTOR.asCoroutineDispatcher(),
      coroutineScope: CoroutineScope = GlobalScope
  ) = CachedNetworkListingCreator.createListing(
      cachedDataSourceAdapter = cachedDataSourceAdapter,
      firstPage = FountainConstants.DEFAULT_FIRST_PAGE,
      ioDatabaseExecutor = ioDatabaseCoroutineDispatcher.toExecutor(coroutineScope),
      ioServiceExecutor = ioServiceCoroutineDispatcher.toExecutor(coroutineScope),
      pagedListConfig = FountainConstants.DEFAULT_PAGED_LIST_CONFIG,
      networkDataSourceAdapter = notPagedCoroutinePageFetcher.toBaseNetworkDataSourceAdapter()
  )
}
