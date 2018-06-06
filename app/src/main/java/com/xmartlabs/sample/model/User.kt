package com.xmartlabs.template.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey var id: Long,
    @SerializedName("login") var name: String?,
    var avatarUrl: String?
)
