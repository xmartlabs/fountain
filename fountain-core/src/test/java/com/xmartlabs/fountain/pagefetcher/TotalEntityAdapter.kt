package com.xmartlabs.fountain.pagefetcher

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.common.FountainConstants
import com.xmartlabs.fountain.common.KnownSizeResponseManager


/**
 * Provides a [NetworkDataSourceAdapter] implementation of a [ListResponseWithEntityCount] response.
 * It is used when the service returns the entity count in the response.
 */
class NetworkDataSourceWithTotalEntityCountAdapter<T, R : ListResponseWithEntityCount<T>>(
    basePageFetcher: BasePageFetcher<R>,
    firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
) : BaseNetworkDataSourceAdapter<ListResponse<T>> {
  private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

  override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)

  override val pageFetcher = object : BasePageFetcher<ListResponse<T>> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<ListResponse<T>>) =
        basePageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<R> {
          override fun onSuccess(response: R) {
            knownSizeResponseManager.onTotalEntityResponseArrived(response)
            networkResultListener.onSuccess(response)
          }

          override fun onError(t: Throwable) = networkResultListener.onError(t)
        })
  }
}

/**
 * Provides a [NetworkDataSourceAdapter] implementation of a [ListResponseWithPageCount] response.
 * It is used when the service returns the page count in the response.
 */
class NetworkDataSourceWithTotalPageCountAdapter<T, R : ListResponseWithPageCount<T>>(
    basePageFetcher: BasePageFetcher<R>,
    firstPage: Int = FountainConstants.DEFAULT_FIRST_PAGE
) : BaseNetworkDataSourceAdapter<ListResponse<T>> {
  private val knownSizeResponseManager = KnownSizeResponseManager(firstPage)

  override fun canFetch(page: Int, pageSize: Int) = knownSizeResponseManager.canFetch(page, pageSize)

  override val pageFetcher = object : BasePageFetcher<ListResponse<T>> {
    @WorkerThread
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<ListResponse<T>>) =
        basePageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<R> {
          override fun onSuccess(response: R) {
            knownSizeResponseManager.onTotalPageCountResponseArrived(pageSize, response)
            networkResultListener.onSuccess(response)
          }

          override fun onError(t: Throwable) = networkResultListener.onError(t)
        })
  }
}
