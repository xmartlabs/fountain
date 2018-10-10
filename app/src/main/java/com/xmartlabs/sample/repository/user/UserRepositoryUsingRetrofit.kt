package com.xmartlabs.sample.repository.user

import android.arch.paging.PagedList
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.retrofit.FountainRetrofit
import com.xmartlabs.fountain.retrofit.adapter.RetrofitPageFetcher
import com.xmartlabs.fountain.retrofit.adapter.toTotalEntityCountNetworkDataSourceAdapter
import com.xmartlabs.sample.db.AppDb
import com.xmartlabs.sample.db.UserDao
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.service.GhListResponse
import com.xmartlabs.sample.service.UserService
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryUsingRetrofit @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao,
    private val db: AppDb
) : UserRepository {
  override fun searchServiceUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : RetrofitPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Call<GhListResponse<User>> =
          userService.searchUsersUsingRetrofit(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = pageFetcher.toTotalEntityCountNetworkDataSourceAdapter()
    return FountainRetrofit.createNetworkListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }

  override fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : RetrofitPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Call<GhListResponse<User>> =
          userService.searchUsersUsingRetrofit(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = pageFetcher.toTotalEntityCountNetworkDataSourceAdapter()

    val cachedDataSourceAdapter = UserCachedDataSourceAdapter(userName, userDao, db)
    return FountainRetrofit.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        cachedDataSourceAdapter = cachedDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }
}
