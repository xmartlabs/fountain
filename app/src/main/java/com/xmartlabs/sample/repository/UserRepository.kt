package com.xmartlabs.sample.repository

import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.fountain.retrofit.FountainRetrofitSupport
import com.xmartlabs.fountain.retrofit.adapter.NetworkDataSourceAdapterFactory
import com.xmartlabs.fountain.retrofit.adapter.RetrofitPageFetcher
import com.xmartlabs.sample.db.AppDb
import com.xmartlabs.sample.db.UserDao
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.UserSearch
import com.xmartlabs.sample.model.service.GhListResponse
import com.xmartlabs.sample.service.UserService
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao,
    private val db: AppDb
) {
  @MainThread
  fun searchServiceUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : RetrofitPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Call<GhListResponse<User>> =
          userService.searchUsersUsingRetrofit(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(
        pageFetcher = pageFetcher)
    return FountainRetrofitSupport.createNetworkListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }

  @MainThread
  fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : RetrofitPageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Call<GhListResponse<User>> =
          userService.searchUsersUsingRetrofit(userName, page = page, pageSize = pageSize)
    })

    val networkDataSourceAdapter = NetworkDataSourceAdapterFactory.fromTotalEntityCountListResponse(
        pageFetcher = pageFetcher)

    val cachedDataSourceAdapter = object : CachedDataSourceAdapter<User, User> {
      override fun getDataSourceFactory() = userDao.findUsersByName(userName)

      override fun saveEntities(response: List<User>) {
        val start = userDao.getNextIndexInUserSearch(userName)
        val relationItems = response
            .mapIndexed { index, user ->
              UserSearch(search = userName, userId = user.id, searchPosition = start + index)
            }
        userDao.insert(response)
        userDao.insertUserSearch(relationItems)
      }

      override fun runInTransaction(transaction: () -> Unit) {
        db.runInTransaction(transaction)
      }

      override fun dropEntities() {
        userDao.deleteUserSearch(userName)
      }
    }
    return FountainRetrofitSupport.createNetworkWithCacheSupportListing(
        networkDataSourceAdapter = networkDataSourceAdapter,
        cachedDataSourceAdapter = cachedDataSourceAdapter,
        pagedListConfig = pagedListConfig
    )
  }
}
