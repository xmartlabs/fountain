package com.xmartlabs.sample.repository.user

import com.xmartlabs.fountain.adapter.CachedDataSourceAdapter
import com.xmartlabs.sample.db.AppDb
import com.xmartlabs.sample.db.UserDao
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.UserSearch

class UserCachedDataSourceAdapter(
    private val userName: String,
    private val userDao: UserDao,
    private val db: AppDb
) : CachedDataSourceAdapter<User, User> {
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
