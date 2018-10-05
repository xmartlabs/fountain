package com.xmartlabs.sample.service

import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.service.GhListResponse
import io.reactivex.Single
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
  companion object {
    private const val SEARCH_USERS_URL = "/search/users"
  }

  @GET(SEARCH_USERS_URL)
  fun searchUsersUsingRx(@Query("q") name: String,
                         @Query("page") page: Int,
                         @Query("per_page") pageSize: Int
  ): Single<GhListResponse<User>>

  @GET(SEARCH_USERS_URL)
  fun searchUsersUsingRetrofit(@Query("q") name: String,
                               @Query("page") page: Int,
                               @Query("per_page") pageSize: Int
  ): Call<GhListResponse<User>>

  @GET(SEARCH_USERS_URL)
  fun searchUsersUsingCoroutines(@Query("q") name: String,
                                 @Query("page") page: Int,
                                 @Query("per_page") pageSize: Int
  ): Deferred<GhListResponse<User>>
}
