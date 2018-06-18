package com.xmartlabs.sample.repository

import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.xmartlabs.fountain.Fountain
import com.xmartlabs.fountain.Listing
import com.xmartlabs.fountain.feature.cachednetwork.DataSourceEntityHandler
import com.xmartlabs.fountain.fetcher.PageFetcher
import com.xmartlabs.fountain.fetcher.PagingHandlerWithTotalEntityCount
import com.xmartlabs.sample.db.AppDb
import com.xmartlabs.sample.db.UserDao
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.UserSearch
import com.xmartlabs.sample.model.service.GhListResponse
import com.xmartlabs.sample.service.UserService
import io.reactivex.Single
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
    val pageFetcher = (object : PageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Single<GhListResponse<User>> =
          userService.searchUsers(userName, page = page, pageSize = pageSize)
    })

    val pagingHandler = PagingHandlerWithTotalEntityCount(pageFetcher = pageFetcher)
    return Fountain.createNetworkListing(
        pagingHandler = pagingHandler,
        pagedListConfig = pagedListConfig
    )
  }

  @MainThread
  fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher = (object : PageFetcher<GhListResponse<User>> {
      override fun fetchPage(page: Int, pageSize: Int): Single<GhListResponse<User>> =
          userService.searchUsers(userName, page = page, pageSize = pageSize)
    })
    val pagingHandler = PagingHandlerWithTotalEntityCount(pageFetcher = pageFetcher)

    val dataSourceEntityHandler = object : DataSourceEntityHandler<User> {
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
    return Fountain.createNetworkWithCacheSupportListing(
        dataSourceEntityHandler = dataSourceEntityHandler,
        pagedListConfig = pagedListConfig,
        pagingHandler = pagingHandler
    )
  }
}
