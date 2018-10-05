package com.xmartlabs.fountain.retrofit.common

import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.ListResponseWithEntityCount
import com.xmartlabs.fountain.ListResponseWithPageCount
import com.xmartlabs.fountain.retrofit.adapter.RetrofitNetworkDataSourceAdapter
import com.xmartlabs.fountain.retrofit.adapter.RetrofitPageFetcher
import com.xmartlabs.fountain.testutils.extensions.generateSpecificIntPageResponseList
import com.xmartlabs.fountain.testutils.extensions.toListResponse
import com.xmartlabs.fountain.testutils.extensions.toListResponseEntityCount
import com.xmartlabs.fountain.testutils.extensions.toListResponsePageCount
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MockedPageFetcher(var error: Boolean = false) : RetrofitPageFetcher<ListResponse<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Call<ListResponse<Int>> =
      if (error) createErrorCall() else generateServiceCall(page)

  private fun generateServiceCall(page: Int) = generateSpecificIntPageResponseList(page)
      .toListResponse()
      .toCall()
}

fun <T: ListResponse<*>> RetrofitPageFetcher<T>.toInfiniteRetrofitNetworkDataSourceAdapter() =
    object : RetrofitNetworkDataSourceAdapter<T> {
      override val retrofitPageFetcher = this@toInfiniteRetrofitNetworkDataSourceAdapter
      override fun canFetch(page: Int, pageSize: Int) = true
    }

class EntityCountMockedPageFetcher(private val entityCount: Long) : RetrofitPageFetcher<ListResponseWithEntityCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Call<ListResponseWithEntityCount<Int>> =
      generateSpecificIntPageResponseList(page)
          .filter { it < entityCount }
          .toListResponseEntityCount(entityCount)
          .toCall()
}

class PageCountMockedPageFetcher(private val pageCount: Long) : RetrofitPageFetcher<ListResponseWithPageCount<Int>> {
  override fun fetchPage(page: Int, pageSize: Int): Call<ListResponseWithPageCount<Int>> =
      generateSpecificIntPageResponseList(page).toListResponsePageCount(pageCount).toCall()
}


@Suppress("ThrowsCount")
private fun <T, ServiceResponse : ListResponse<T>> ServiceResponse.toCall(): Call<ServiceResponse> {
  return object : Call<ServiceResponse> {
    override fun clone(): Call<ServiceResponse> = throw IllegalStateException("Not implemented")

    override fun enqueue(callback: Callback<ServiceResponse>?) = throw IllegalStateException("Not implemented")

    override fun isExecuted(): Boolean = throw IllegalStateException("Not implemented")

    override fun isCanceled(): Boolean = throw IllegalStateException("Not implemented")

    override fun cancel() = throw IllegalStateException("Not implemented")

    override fun execute(): Response<ServiceResponse> = Response.success(this@toCall)

    override fun request(): Request = throw IllegalStateException("Not implemented")
  }
}

@Suppress("ThrowsCount")
private fun <ServiceResponse> createErrorCall(): Call<ServiceResponse> {
  return object : Call<ServiceResponse> {
    override fun clone(): Call<ServiceResponse> = throw IllegalStateException("Not implemented")

    override fun enqueue(callback: Callback<ServiceResponse>?) = throw IllegalStateException("Not implemented")

    override fun isExecuted(): Boolean = throw IllegalStateException("Not implemented")

    override fun isCanceled(): Boolean = throw IllegalStateException("Not implemented")

    override fun cancel() = throw IllegalStateException("Not implemented")

    override fun execute(): Response<ServiceResponse> = Response.error(421,
        ResponseBody.create(null, "Throwable"))

    override fun request(): Request = throw IllegalStateException("Not implemented")
  }
}
