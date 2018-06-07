package com.xmartlabs.xlpagingbypagenumber.feature.cachednetwork

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.xlpagingbypagenumber.Listing
import com.xmartlabs.xlpagingbypagenumber.fetcher.PagingHandler
import java.util.concurrent.Executor

internal object CacheNetworkListingCreator {
  fun <Value, ServiceResponse> createListing(
      dataSourceEntityHandler: DataSourceEntityHandler<ServiceResponse>,
      dataSourceFactory: DataSource.Factory<*, Value>,
      firstPage: Int,
      ioDatabaseExecutor: Executor,
      ioServiceExecutor: Executor,
      pagedListConfig: PagedList.Config,
      pagingHandler: PagingHandler<out ServiceResponse>
  ): Listing<Value> {

    val boundaryCallback = BoundaryCallback<Value, ServiceResponse>(
        pageFetcher = pagingHandler,
        firstPage = firstPage,
        dataSourceEntityHandler = dataSourceEntityHandler,
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
