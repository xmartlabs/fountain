package com.xmartlabs.template.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.android.example.github.db.UserDao
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
