/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.github.db

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.xmartlabs.template.model.User
import com.xmartlabs.template.model.UserSearch

/**
 * Interface for database access for User related operations.
 */
@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(users: List<User>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUserSearch(posts: List<UserSearch>)

  @Query("SELECT * FROM User INNER JOIN UserSearch ON User.id = UserSearch.userId " +
      "WHERE search=:search ORDER BY searchPosition ASC")
  fun findUsersByName(search: String): DataSource.Factory<Int, User>

  @Query("SELECT MAX(searchPosition) + 1 FROM UserSearch WHERE search=:search")
  fun getNextIndexInUserSearch(search: String): Long

  @Query("DELETE FROM UserSearch WHERE search=:search")
  fun deleteUserSearch(search: String)
}
