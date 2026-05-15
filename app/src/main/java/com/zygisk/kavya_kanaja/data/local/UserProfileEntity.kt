package com.zygisk.kavya_kanaja.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val gender: String,
    val phoneNumber: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
