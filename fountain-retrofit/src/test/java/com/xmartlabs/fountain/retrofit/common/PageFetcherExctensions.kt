package com.xmartlabs.fountain.retrofit.common

import com.xmartlabs.fountain.adapter.NetworkResultListener
import com.xmartlabs.fountain.adapter.PageFetcher
import com.xmartlabs.fountain.retrofit.adapter.RetrofitPageFetcher
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("ComplexMethod", "ThrowsCount")
fun <T> PageFetcher<T>.toRxPageFetcher(): RetrofitPageFetcher<T> {
  return object : RetrofitPageFetcher<T> {
    override fun fetchPage(page: Int, pageSize: Int): Call<T> {
      return object : Call<T> {
        override fun enqueue(callback: Callback<T>?) {
          val lastCall = this
          this@toRxPageFetcher.fetchPage(page, pageSize, object : NetworkResultListener<T>{
            override fun onSuccess(response: T) {
              callback?.onResponse(lastCall, Response.success(response))
            }

            override fun onError(t: Throwable) {
              throw IllegalStateException("Not implemented")
            }
          })
        }

        override fun isExecuted(): Boolean {
          throw IllegalStateException("Not implemented")
        }

        override fun clone(): Call<T> {
          throw IllegalStateException("Not implemented")
        }

        override fun isCanceled(): Boolean {
          throw IllegalStateException("Not implemented")
        }

        override fun cancel() {
          throw IllegalStateException("Not implemented")
        }

        override fun execute(): Response<T> {
          throw IllegalStateException("Not implemented")
        }

        override fun request(): Request {
          throw IllegalStateException("Not implemented")
        }
      }
    }
  }
}
