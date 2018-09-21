package com.xmartlabs.fountain.rx2.common

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import com.xmartlabs.fountain.rx2.adapter.RxPageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.toListResponseEntityCount
import com.xmartlabs.fountain.testutils.extensions.toListResponsePageCount
import io.reactivex.Single

fun <T> PageFetcher<T>.toRxPageFetcher(): RxPageFetcher<T> {
  return object : RxPageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int): Single<T> {
      return Single.create {
        fetchPage(page, pageSize, object : NetworkResultListener<T> {
          override fun onSuccess(response: T) = it.onSuccess(response)

          override fun onError(t: Throwable) = it.onError(t)
        })
      }
    }
  }
}

class EntityCountMockedPageFetcher(private val entityCount: Long) : RxPageFetcher<ListResponseWithEntityCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseWithEntityCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .filter { it < entityCount }
          .toListResponseEntityCount(entityCount)
          .toSingle()
}

class PageCountMockedPageFetcher(private val pageCount: Long) : RxPageFetcher<ListResponseWithPageCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Single<ListResponseWithPageCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .toListResponsePageCount(pageCount)
          .toSingle()
}

private fun <T> T.toSingle(): Single<T> {
  return Single.just(this)
}
