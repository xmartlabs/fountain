package com.xmartlabs.fountain.retrofit.adapter

import android.support.annotation.WorkerThread
import com.xmartlabs.fountain.ListResponse
import com.xmartlabs.fountain.adapter.BaseNetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.BasePageFetcher
import com.xmartlabs.fountain.adapter.NetworkResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

private fun <T : ListResponse<*>> RetrofitPageFetcher<T>.toBasePageFetcher(): BasePageFetcher<T> {
  return object : BasePageFetcher<T> {
    @WorkerThread
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      @Suppress("TooGenericExceptionCaught")
      try {
        val response = this@toBasePageFetcher.fetchPage(page = page, pageSize = pageSize).execute()
        if (response.isSuccessful) {
          response.body()
              ?.let { networkResultListener.onSuccess(it) }
              .orDo {
                networkResultListener.onError(
                    IllegalStateException("Response body cannot be null", HttpException(response))
                )
              }
        } else {
          networkResultListener.onError(HttpException(response))
        }
      } catch (throwable: Throwable) {
        networkResultListener.onError(throwable)
      }
    }
  }
}

internal fun <T : ListResponse<*>> RetrofitNetworkDataSourceAdapter<T>.toBaseNetworkDataSourceAdapter() =
    object : BaseNetworkDataSourceAdapter<T> {
      override val pageFetcher = this@toBaseNetworkDataSourceAdapter.pageFetcher.toBasePageFetcher()

      override fun canFetch(page: Int, pageSize: Int): Boolean =
          this@toBaseNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
    }

@Suppress("ComplexMethod")
internal fun <T> Call<T>.doOnSuccess(onSuccessResponse: (T) -> Unit): Call<T> {
  return object : Call<T> {
    override fun enqueue(callback: Callback<T>?) {
      this@doOnSuccess.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) =
            callback?.onFailure(call, t) ?: Unit

        override fun onResponse(call: Call<T>, response: Response<T>) {
          response.body()
              ?.let {
                callback?.onResponse(call, response)
                onSuccessResponse.invoke(it)
              }
              .orDo { callback?.onFailure(call, IllegalStateException("Response cannot be null")) }
        }
      })
    }

    override fun isExecuted() = this@doOnSuccess.isExecuted

    override fun clone() = this@doOnSuccess.clone()

    override fun isCanceled() = this@doOnSuccess.isCanceled

    override fun cancel() = this@doOnSuccess.cancel()

    override fun execute(): Response<T> {
      val response = this@doOnSuccess.execute()
      if (response.isSuccessful) {
        response.body()?.let { onSuccessResponse.invoke(it) }
      }
      return response
    }

    override fun request() = this@doOnSuccess.request()
  }
}

private fun <T> T?.orDo(action: () -> T) = this ?: action()
