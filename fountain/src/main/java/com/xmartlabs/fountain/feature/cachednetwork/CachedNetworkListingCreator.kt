package com.xmartlabs.fountain.feature.cachednetwork

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.fetcher.PagingHandler
import java.util.concurrent.Executor

internal object CachedNetworkListingCreator {
  fun <Value, ServiceResponse : ListResponse<Value>> createListing(
      dataSourceEntityHandler: DataSourceEntityHandler<Value>,
      firstPage: Int,
      ioDatabaseExecutor: Executor,
      ioServiceExecutor: Executor,
      pagedListConfig: PagedList.Config,
      pagingHandler: PagingHandler<out ServiceResponse>
  ): Listing<Value> {

    val boundaryCallback = BoundaryCallback(
        pageFetcher = pagingHandler,
        firstPage = firstPage,
        dataSourceEntityHandler = dataSourceEntityHandler,
        pagedListConfig = pagedListConfig,
        ioDatabaseExecutor = ioDatabaseExecutor,
        ioServiceExecutor = ioServiceExecutor
    )

    val builder = LivePagedListBuilder(dataSourceEntityHandler.getDataSourceFactory(), pagedListConfig)
        .setBoundaryCallback(boundaryCallback)

    val refreshTrigger = MutableLiveData<Unit>()
    val refreshState = Transformations.switchMap(refreshTrigger) {
      boundaryCallback.resetData()
    }

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
