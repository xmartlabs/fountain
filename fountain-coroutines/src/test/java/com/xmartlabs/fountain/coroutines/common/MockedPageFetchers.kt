package com.xmartlabs.fountain.coroutines.common

import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.coroutines.adapter.CoroutinePageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.toListResponseEntityCount
import com.xmartlabs.fountain.testutils.extensions.toListResponsePageCount
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred

class EntityCountMockedPageFetcher(private val entityCount: Long) : CoroutinePageFetcher<ListResponseWithEntityCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Deferred<ListResponseWithEntityCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .filter { it < entityCount }
          .toListResponseEntityCount(entityCount)
          .toDeferred()
}

class PageCountMockedPageFetcher(private val pageCount: Long) : CoroutinePageFetcher<ListResponseWithPageCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Deferred<ListResponseWithPageCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .toListResponsePageCount(pageCount)
          .toDeferred()
}

private fun <T> T.toDeferred(): Deferred<T> {
  val deferred = CompletableDeferred<T>()
  deferred.complete(this)
  return deferred
}
