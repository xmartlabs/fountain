package com.xmartlabs.fountain.rx2

import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.feature.cachednetwork.CachedNetworkListingCreator
import com.xmartlabs.fountain.feature.network.NetworkPagedListingCreator
import com.xmartlabs.fountain.rx2.adapter.NotPagedRxPageFetcher
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.adapter.toBaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.common.toExecutor
import com.xmartlabs.fountain.rx2.common.toScheduler
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/** A [Listing] factory */
object FountainRx {
  /**
   * Creates a [Listing] with Network support.
   *
   * @param NetworkValue The listed entity type.
   * @param networkDataSourceAdapter The [RxNetworkDataSourceAdapter] to manage the paged service endpoint.
   * @param firstPage The first page number, defined by the service.
   * The default value is 1.
   * @param ioServiceScheduler The [Scheduler] with which the service call will be made.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue> createNetworkListing(
      networkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<NetworkValue>>,
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE,
      ioServiceScheduler: Scheduler = Schedulers.io(),
      pagedListConfig: PagedList.Config = FountainConstants.DEFAULT_PAGED_LIST_CONFIG
  ) = NetworkPagedListingCreator.createListing(
      firstPage = firstPage,
      ioServiceExecutor = ioServiceScheduler.toExecutor(),
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter.toBaseNetworkDataSourceAdapter()
  )

  /**
   * Creates a [Listing] with Network support.
   *
   * @param NetworkValue The listed entity type.
   * @param notPagedRxPageFetcher The [NotPagedRxPageFetcher] that is used to perform the service requests.
   * @param ioServiceScheduler The [Scheduler] with which the service call will be made.
   * @return A [Listing] structure with Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue> createNotPagedNetworkListing(
      notPagedRxPageFetcher: NotPagedRxPageFetcher<out ListResponse<NetworkValue>>,
      ioServiceScheduler: Scheduler = Schedulers.io()
  ) = NetworkPagedListingCreator.createListing(
      firstPage = FountainConstants.DEFAULT_FIRST_PAGE,
      ioServiceExecutor = ioServiceScheduler.toExecutor(),
      pagedListConfig = FountainConstants.DEFAULT_PAGED_LIST_CONFIG,
      networkDataSourceAdapter = notPagedRxPageFetcher.toBaseNetworkDataSourceAdapter()
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
   * @param ioServiceScheduler The [Scheduler] with which the service call will be made.
   * @param ioDatabaseScheduler The [Scheduler] through which the database transactions will be made.
   * By default the library will use a single thread.
   * @param pagedListConfig The paged list configuration.
   * In this object you can specify several options, for example the [pageSize][PagedList.Config.pageSize]
   * and the [initialPageSize][PagedList.Config.initialLoadSizeHint].
   * @return A [Listing] structure with Cache + Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, DataSourceValue> createNetworkWithCacheSupportListing(
      networkDataSourceAdapter: RxNetworkDataSourceAdapter<out ListResponse<out NetworkValue>>,
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      ioServiceScheduler: Scheduler = Schedulers.io(),
      ioDatabaseScheduler: Scheduler = FountainConstants.DATABASE_EXECUTOR.toScheduler(),
      firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE,
      pagedListConfig: PagedList.Config = FountainConstants.DEFAULT_PAGED_LIST_CONFIG
  ) = CachedNetworkListingCreator.createListing(
      cachedDataSourceAdapter = cachedDataSourceAdapter,
      firstPage = firstPage,
      ioDatabaseExecutor = ioDatabaseScheduler.toExecutor(),
      ioServiceExecutor = ioServiceScheduler.toExecutor(),
      pagedListConfig = pagedListConfig,
      networkDataSourceAdapter = networkDataSourceAdapter.toBaseNetworkDataSourceAdapter()
  )

  /**
   * Creates a [Listing] with Cache + Network Support from a not paged endpoint.
   *
   * @param NetworkValue The network entity type.
   * @param DataSourceValue The [DataSource] entity type.
   * @param notPagedRxPageFetcher The [NotPagedRxPageFetcher] that is used to perform the service requests.
   * @param cachedDataSourceAdapter The [CachedDataSourceAdapter] to take control of the [DataSource].
   * @param ioServiceScheduler The [Scheduler] with which the service call will be made.
   * @param ioDatabaseScheduler The [Scheduler] through which the database transactions will be made.
   * By default the library will use a single thread.
   * @return A [Listing] structure with Cache + Network Support.
   */
  @Suppress("LongParameterList")
  fun <NetworkValue, DataSourceValue> createNotPagedNetworkWithCacheSupportListing(
      notPagedRxPageFetcher: NotPagedRxPageFetcher<out ListResponse<out NetworkValue>>,
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      ioServiceScheduler: Scheduler = Schedulers.io(),
      ioDatabaseScheduler: Scheduler = FountainConstants.DATABASE_EXECUTOR.toScheduler()
  ) = CachedNetworkListingCreator.createListing(
      cachedDataSourceAdapter = cachedDataSourceAdapter,
      firstPage = FountainConstants.DEFAULT_FIRST_PAGE,
      ioDatabaseExecutor = ioDatabaseScheduler.toExecutor(),
      ioServiceExecutor = ioServiceScheduler.toExecutor(),
      pagedListConfig = FountainConstants.DEFAULT_PAGED_LIST_CONFIG,
      networkDataSourceAdapter = notPagedRxPageFetcher.toBaseNetworkDataSourceAdapter()
  )
}
