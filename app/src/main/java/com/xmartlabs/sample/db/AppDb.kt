package com.xmartlabs.sample.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.xmartlabs.template.model.User
import com.xmartlabs.template.model.UserSearch

@Database(
    entities = [User::class, UserSearch::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
  abstract fun userDao(): UserDao
}
