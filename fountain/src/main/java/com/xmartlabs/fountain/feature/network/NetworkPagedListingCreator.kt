package com.xmartlabs.template.repository.common

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.feature.network.NetworkPagedDataSourceFactory
import com.xmartlabs.fountain.fetcher.ListResponsePagingHandler
import java.util.concurrent.Executor

internal object NetworkPagedListingCreator {
  fun <T> createListing(
      firstPage: Int,
      ioServiceExecutor: Executor,
      pagedListConfig: PagedList.Config,
      pagingHandler: ListResponsePagingHandler<T>
  ): Listing<T> {
    val sourceFactory = NetworkPagedDataSourceFactory(
        firstPage = firstPage,
        ioServiceExecutor = ioServiceExecutor,
        pagedListConfig = pagedListConfig,
        pagingHandler = pagingHandler
    )
    val livePagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
        .build()

    val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
      it.initialLoad
    }
    return Listing(
        pagedList = livePagedList,
        networkState = Transformations.switchMap(sourceFactory.sourceLiveData, {
          it.networkState
        }),
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
