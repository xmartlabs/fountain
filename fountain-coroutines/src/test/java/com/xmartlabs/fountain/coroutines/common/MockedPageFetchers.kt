package com.xmartlabs.fountain.coroutines.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.coroutines.adapter.CoroutineNetworkDataSourceAdapter
import com.xmartlabs.fountain.coroutines.adapter.CoroutinePageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.toListResponse
import com.xmartlabs.fountain.testutils.extensions.toListResponseEntityCount
import com.xmartlabs.fountain.testutils.extensions.toListResponsePageCount
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred

class MockedPageFetcher(var error: Boolean = false) : CoroutinePageFetcher<ListResponse<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Deferred<ListResponse<Int>> =
      if (error) IllegalStateException("Mocked error").toErrorDeferred() else generateServiceCall(page)

  private fun generateServiceCall(page: Int) = generateSpecificIntPageResponseList(page)
      .toListResponse()
      .toDeferred()
}

fun <T: ListResponse<*>> CoroutinePageFetcher<T>.toInfiniteCoroutineNetworkDataSourceAdapter() =
    object : CoroutineNetworkDataSourceAdapter<T> {
      override val coroutinePageFetcher = this@toInfiniteCoroutineNetworkDataSourceAdapter
      override fun canFetch(page: Int, pageSize: Int) = true
    }

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

private fun <T> Throwable.toErrorDeferred(): Deferred<T> {
  val deferred = CompletableDeferred<T>()
  deferred.completeExceptionally(this)
  return deferred
}

private fun <T> T.toDeferred(): Deferred<T> {
  val deferred = CompletableDeferred<T>()
  deferred.complete(this)
  return deferred
}
