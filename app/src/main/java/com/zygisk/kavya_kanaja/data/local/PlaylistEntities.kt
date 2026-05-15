package com.zygisk.kavya_kanaja.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_poem_cross_ref", primaryKeys = ["playlistId", "poemId"])
data class PlaylistPoemCrossRef(
    val playlistId: Int,
    val poemId: Int
)
