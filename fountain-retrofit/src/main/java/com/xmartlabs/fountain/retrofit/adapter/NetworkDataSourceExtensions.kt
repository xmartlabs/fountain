package com.xmartlabs.fountain.retrofit.adapter

import com.xmartlabs.fountain.adapter.NetworkDataSourceAdapter
import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

private fun <T> RetrofitPageFetcher<T>.toPageFetcher(): PageFetcher<T> {
  return object : PageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int, networkResultListener: NetworkResultListener<T>) {
      try {
        val response = this@toPageFetcher.fetchPage(page = page, pageSize = pageSize).execute()
        if (response.isSuccessful) {
          networkResultListener.onSuccess(response.body()!!)
        } else {
          networkResultListener.onError(HttpException(response))
        }
      } catch (throwable: Throwable) {
        networkResultListener.onError(throwable)
      }
    }
  }
}

internal fun <T> RetrofitNetworkDataSourceAdapter<T>.toNetworkDataSourceAdapter(): NetworkDataSourceAdapter<T> {
  return object : NetworkDataSourceAdapter<T> {
    override val pageFetcher = retrofitPageFetcher.toPageFetcher()

    override fun canFetch(page: Int, pageSize: Int): Boolean =
        this@toNetworkDataSourceAdapter.canFetch(page = page, pageSize = pageSize)
  }
}

@Suppress("ComplexMethod")
internal fun <T> Call<T>.doOnSuccess(onSuccessResponse: (T) -> Unit): Call<T> {
  return object : Call<T> {
    override fun enqueue(callback: Callback<T>?) {
      this@doOnSuccess.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) =
            callback?.onFailure(call, t) ?: Unit

        override fun onResponse(call: Call<T>?, response: Response<T>) {
          response.body().let {
            if (it == null){
              callback?.onFailure(call, IllegalStateException("Response cannot be null"))
            }else {
              onSuccessResponse.invoke(it)
            }
          }
          callback?.onResponse(call, response)
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
