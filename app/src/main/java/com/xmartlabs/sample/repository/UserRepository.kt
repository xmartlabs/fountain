package com.xmartlabs.sample.repository

import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.android.example.github.db.UserDao
import com.xmartlabs.sample.model.service.GhListResponse
import com.xmartlabs.sample.service.UserService
import com.xmartlabs.template.db.AppDb
import com.xmartlabs.template.model.User
import com.xmartlabs.template.model.UserSearch
import com.xmartlabs.template.repository.common.ServicePagedListingCreator
import com.xmartlabs.xlpagingbypagenumber.ListResponse
import com.xmartlabs.xlpagingbypagenumber.Listing
import com.xmartlabs.xlpagingbypagenumber.common.ListResponsePageFetcher
import com.xmartlabs.xlpagingbypagenumber.common.PageFetcher
import com.xmartlabs.xlpagingbypagenumber.dbsupport.DatabaseEntitiesHandler
import com.xmartlabs.xlpagingbypagenumber.dbsupport.ServiceAndDatabasePagedListingCreator
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
    val pageFetcher = (object : ListResponsePageFetcher<User> {
      override fun canFetch(page: Int): Boolean = true //todo:

      override fun getPage(page: Int, pageSize: Int): Single<GhListResponse<User>> =
          userService.searchUsers(userName, page = page, pageSize = pageSize)
    })

    return ServicePagedListingCreator.createListing(pageFetcher = pageFetcher, pagedListConfig = pagedListConfig)
  }

  @MainThread
  fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User> {
    val pageFetcher: PageFetcher<GhListResponse<User>> = (object : PageFetcher<GhListResponse<User>> {
      override fun canFetch(page: Int): Boolean = true //todo:

      override fun getPage(page: Int, pageSize: Int) =
          userService.searchUsers(userName, page = page, pageSize = pageSize)
    })

    val databaseFunctionsHandler = object : DatabaseEntitiesHandler<ListResponse<User>> {
      override fun runInTransaction(transaction: () -> Unit) {
        db.runInTransaction(transaction)
      }

      override fun saveEntities(response: ListResponse<User>?) {
        response?.getElements()?.let { users ->
          val start = userDao.getNextIndexInUserSearch(userName)
          val relationItems = users
              .mapIndexed { index, user ->
                UserSearch(search = userName, userId = user.id, searchPosition = start + index)
              }
          userDao.insert(users)
          userDao.insertUserSearch(relationItems)
        }
      }

      override fun dropEntities() {
        userDao.deleteUserSearch(userName)
      }
    }
    return ServiceAndDatabasePagedListingCreator.createListing(
        databaseEntitiesHandler = databaseFunctionsHandler,
        dataSourceFactory = userDao.findUsersByName(userName),
        pagedListConfig = pagedListConfig,
        pageFetcher = pageFetcher
    )
  }
}
