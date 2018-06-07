package com.xmartlabs.xlpagingbypagenumber.dbsupport

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.xlpagingbypagenumber.Listing
import com.xmartlabs.xlpagingbypagenumber.common.IoExecutors
import com.xmartlabs.xlpagingbypagenumber.fetcher.PagingHandler
import java.util.concurrent.Executor

object ServiceAndDatabasePagedListingCreator {
  private const val DEFAULT_NETWORK_PAGE_SIZE = 30
  internal val DEFAULT_PAGED_LIST_CONFIG = PagedList.Config.Builder()
      .setPageSize(DEFAULT_NETWORK_PAGE_SIZE)
      .build()

  fun <Value, ServiceResponse> createListing(
      dataSourceFactory: DataSource.Factory<*, Value>,
      pagingHandler: PagingHandler<out ServiceResponse>,
      databaseEntityHandler: DatabaseEntityHandler<ServiceResponse>,
      ioServiceExecutor: Executor = IoExecutors.NETWORK_EXECUTOR,
      ioDatabaseExecutor: Executor = IoExecutors.DATABASE_EXECUTOR,
      firstPage: Int = 1,
      pagedListConfig: PagedList.Config = PagedList.Config.Builder().setPageSize(DEFAULT_NETWORK_PAGE_SIZE).build()
  ): Listing<Value> {

    val boundaryCallback = BoundaryCallback<Value, ServiceResponse>(
        pageFetcher = pagingHandler,
        firstPage = firstPage,
        databaseEntityHandler = databaseEntityHandler,
        pagedListConfig = pagedListConfig,
        ioDatabaseExecutor = ioDatabaseExecutor,
        ioServiceExecutor = ioServiceExecutor
    )

    val builder = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
        .setBoundaryCallback(boundaryCallback)

    val refreshTrigger = MutableLiveData<Unit>()
    val refreshState = Transformations.switchMap(refreshTrigger, {
      boundaryCallback.resetData()
    })

    return Listing(
        pagedList = builder.build(),
        networkState = boundaryCallback.networkState,
        retry = {
          boundaryCallback.helper.retryAllFailed()
        },
        refresh = {
          refreshTrigger.value = null
        },
        refreshState = refreshState
    )
  }
}
