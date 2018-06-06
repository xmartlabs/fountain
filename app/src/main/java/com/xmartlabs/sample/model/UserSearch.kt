package com.xmartlabs.template.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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
