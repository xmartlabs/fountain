package com.xmartlabs.fountain.feature.network

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import java.util.concurrent.Executor

internal object NetworkPagedListingCreator {
  fun <Value> createListing(
      firstPage: Int,
      ioServiceExecutor: Executor,
      pagedListConfig: PagedList.Config,
      networkDataSourceAdapter: NetworkDataSourceAdapter<out ListResponse<Value>>
  ): Listing<Value> {
    val sourceFactory = NetworkPagedDataSourceFactory(
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor,
        pagedListConfig = pagedListConfig,
        networkDataSourceAdapter = networkDataSourceAdapter
    )
    val livePagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
        .build()

    val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
      it.initialLoad
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
          sourceFactory.sourceLiveData.value?.invalidate()
        },
        refreshState = refreshState
    )
  }
}
