package com.xmartlabs.sample.service

import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.service.GhListResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
  @GET("/search/users")
  fun searchUsersUsingRx(@Query("q") name: String,
                         @Query("page") page: Int,
                         @Query("per_page") pageSize: Int
  ): Single<GhListResponse<User>>  @GET("/search/users")

  fun searchUsersUsingRetrofit(@Query("q") name: String,
                               @Query("page") page: Int,
                               @Query("per_page") pageSize: Int
  ): Call<GhListResponse<User>>
}
