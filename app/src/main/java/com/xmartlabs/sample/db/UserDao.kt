package com.xmartlabs.sample.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.UserSearch

/** Interface for database access for User related operations. */
@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(users: List<User>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUserSearch(posts: List<UserSearch>)

  @Query("SELECT User.* FROM User INNER JOIN UserSearch ON User.id = UserSearch.userId " +
      "WHERE search=:search ORDER BY searchPosition ASC")
  fun findUsersByName(search: String): DataSource.Factory<Int, User>

  @Query("SELECT MAX(searchPosition) + 1 FROM UserSearch WHERE search=:search")
  fun getNextIndexInUserSearch(search: String): Long

  @Query("DELETE FROM UserSearch WHERE search=:search")
  fun deleteUserSearch(search: String)
}
