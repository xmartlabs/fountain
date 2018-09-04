package com.xmartlabs.fountain.feature.network

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import java.util.concurrent.Executor

internal object NetworkPagedListingCreator {
  fun <Value, ServiceResponse : ListResponse<Value>> createListing(
      firstPage: Int,
      ioServiceExecutor: Executor,
      pagedListConfig: PagedList.Config,
      networkDataSourceAdapter: NetworkDataSourceAdapter<ServiceResponse>
  ): Listing<Value, ServiceResponse> {
    val sourceFactory = NetworkPagedDataSourceFactory(
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor,
        pagedListConfig = pagedListConfig,
        networkDataSourceAdapter = networkDataSourceAdapter
    )
    val livePagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
        .build()

    val refreshTrigger = MutableLiveData<Unit>()
    val refreshState = Transformations.switchMap(refreshTrigger) {
      sourceFactory.resetData()
    }

    return Listing(
        pagedList = livePagedList,
        networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
          it.networkState
        },
        retry = {
          sourceFactory.sourceLiveData.value?.retryAllFailed()
        },
        refresh = {
          refreshTrigger.value = null
        },
        refreshState = refreshState
    )
  }
}
