package com.xmartlabs.sample.repository

import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.rx2.FountainRx
import com.xmartlabs.fountain.rx2.adapter.RxPageFetcher
import com.xmartlabs.fountain.rx2.adapter.toTotalEntityCountRetrofitNetworkDataSourceAdapter
import com.xmartlabs.sample.db.AppDb
import com.xmartlabs.sample.db.UserDao
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.service.GhListResponse
import com.xmartlabs.sample.service.UserService
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryUsingRx @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao,
    private val db: AppDb
) {
  @MainThread
  fun searchServiceUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : RxPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Single<GhListResponse<User>> =
          userService.searchUsersUsingRx(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = pageFetcher.toTotalEntityCountRetrofitNetworkDataSourceAdapter()
    return FountainRx.createNetworkListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }

  @MainThread
  fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : RxPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Single<GhListResponse<User>> =
          userService.searchUsersUsingRx(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = pageFetcher.toTotalEntityCountRetrofitNetworkDataSourceAdapter()

    val cachedDataSourceAdapter = UserCachedDataSourceAdapter(userName, userDao, db)
    return FountainRx.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        cachedDataSourceAdapter = cachedDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }
}
