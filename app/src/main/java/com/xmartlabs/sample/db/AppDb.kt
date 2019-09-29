package com.xmartlabs.sample.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.model.UserSearch

@Database(
    entities = [User::class, UserSearch::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
  abstract fun userDao(): UserDao
}
