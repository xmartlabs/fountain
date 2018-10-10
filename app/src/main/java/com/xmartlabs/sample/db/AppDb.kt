package com.xmartlabs.sample.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
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
