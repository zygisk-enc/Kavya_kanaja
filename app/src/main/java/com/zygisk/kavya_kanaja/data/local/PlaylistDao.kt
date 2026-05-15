package com.zygisk.kavya_kanaja.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPoemToPlaylist(crossRef: PlaylistPoemCrossRef)

    @Delete
    suspend fun removePoemFromPlaylist(crossRef: PlaylistPoemCrossRef)

    @Query("""
        SELECT * FROM playlist_poem_cross_ref 
        WHERE playlistId = :playlistId
    """)
    fun getPoemIdsInPlaylist(playlistId: Int): Flow<List<PlaylistPoemCrossRef>>
    
    @Query("SELECT COUNT(*) FROM playlists WHERE name = :name")
    suspend fun getPlaylistCountByName(name: String): Int
}
