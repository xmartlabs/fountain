package com.xmartlabs.sample.repository.user

import android.arch.paging.PagedList
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.coroutines.FountainCoroutines
import com.xmartlabs.fountain.coroutines.adapter.CoroutinePageFetcher
import com.xmartlabs.fountain.coroutines.adapter.toTotalEntityCountNetworkDataSourceAdapter
import com.xmartlabs.sample.db.AppDb
import com.xmartlabs.sample.db.UserDao
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.service.GhListResponse
import com.xmartlabs.sample.service.UserService
import kotlinx.coroutines.experimental.Deferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryUsingCoroutines @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao,
    private val db: AppDb
) : UserRepository {
  override fun searchServiceUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : CoroutinePageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Deferred<GhListResponse<User>> =
          userService.searchUsersUsingCoroutines(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = pageFetcher.toTotalEntityCountNetworkDataSourceAdapter()
    return FountainCoroutines.createNetworkListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }

  override fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : CoroutinePageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Deferred<GhListResponse<User>> =
          userService.searchUsersUsingCoroutines(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = pageFetcher.toTotalEntityCountNetworkDataSourceAdapter()

    val cachedDataSourceAdapter = UserCachedDataSourceAdapter(userName, userDao, db)
    return FountainCoroutines.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        cachedDataSourceAdapter = cachedDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }
}
