package com.xmartlabs.fountain.feature.cachednetwork

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import java.util.concurrent.Executor

internal object CachedNetworkListingCreator {
  @Suppress("LongParameterList")
  fun <NetworkValue, DataSourceValue, ServiceResponse : ListResponse<NetworkValue>> createListing(
      cachedDataSourceAdapter: CachedDataSourceAdapter<NetworkValue, DataSourceValue>,
      firstPage: Int,
      ioDatabaseExecutor: Executor,
      ioServiceExecutor: Executor,
      pagedListConfig: PagedList.Config,
      networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>
  ): Listing<DataSourceValue> {

    val boundaryCallback = BoundaryCallback(
        networkDataSourceAdapter = networkDataSourceAdapter,
        firstPage = firstPage,
        cachedDataSourceAdapter = cachedDataSourceAdapter,
        pagedListConfig = pagedListConfig,
        ioDatabaseExecutor = ioDatabaseExecutor,
        ioServiceExecutor = ioServiceExecutor
    )

    val builder = LivePagedListBuilder(cachedDataSourceAdapter.getDataSourceFactory(), pagedListConfig)
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
