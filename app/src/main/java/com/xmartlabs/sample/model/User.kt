package com.xmartlabs.sample.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey var id: Long,
    @SerializedName("login") var name: String?,
    var avatarUrl: String?
)
