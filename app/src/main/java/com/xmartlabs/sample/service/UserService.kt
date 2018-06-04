package com.xmartlabs.sample.service

import com.xmartlabs.template.model.User
import com.xmartlabs.xlpagingbypagenumber.ListResponse
import com.xmartlabs.sample.model.service.GhListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
  @GET("/search/users")
  fun searchUsers(@Query("q") name: String,
                  @Query("page") page: Int,
                  @Query("per_page") pageSize: Int
  ): Single<GhListResponse<User>>
}
