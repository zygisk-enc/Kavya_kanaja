package com.zygisk.kavya_kanaja.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class PoemProgressEntity(
    @PrimaryKey val poemId: Int,
    val isRead: Boolean = false,
    val isFavorite: Boolean = false,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val lastAudioPosition: Int = 0
)