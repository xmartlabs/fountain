package com.xmartlabs.sample.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [
      ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"])
    ],
    indices = [Index("userId")]
)
data class UserSearch(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val search: String,
    val userId: Long,
    val searchPosition: Long
)
