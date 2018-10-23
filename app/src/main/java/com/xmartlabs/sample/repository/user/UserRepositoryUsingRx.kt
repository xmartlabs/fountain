package com.xmartlabs.sample.repository.user

import android.arch.paging.PagedList
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.rx2.FountainRx
import com.xmartlabs.fountain.rx2.adapter.RxNetworkDataSourceAdapter
import com.xmartlabs.fountain.rx2.adapter.RxPageFetcher
import com.xmartlabs.fountain.rx2.adapter.toTotalEntityCountNetworkDataSourceAdapter
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
) : UserRepository {
  override fun searchServiceUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val networkDataSourceAdapter = createNetworkDataSourceAdapter(userName)
    return FountainRx.createNetworkListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }

  override fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val networkDataSourceAdapter = createNetworkDataSourceAdapter(userName)
    val cachedDataSourceAdapter = UserCachedDataSourceAdapter(userName, userDao, db)
    return FountainRx.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        cachedDataSourceAdapter = cachedDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }

  private fun createNetworkDataSourceAdapter(userName: String): RxNetworkDataSourceAdapter<GhListResponse<User>> {
    val pageFetcher = object : RxPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Single<GhListResponse<User>> =
          userService.searchUsersUsingRx(userName, page = page, pageSize = pageSize)
    }
    return pageFetcher.toTotalEntityCountNetworkDataSourceAdapter()
  }
}
