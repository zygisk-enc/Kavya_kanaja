package com.zygisk.kavya_kanaja.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zygisk.kavya_kanaja.data.local.PoemDao
import com.zygisk.kavya_kanaja.data.model.Poem
import com.zygisk.kavya_kanaja.data.model.PoetBio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class PoemRepository(
    private val context: Context,
    private val poemDao: PoemDao,
    private val playlistDao: com.zygisk.kavya_kanaja.data.local.PlaylistDao
) {
    suspend fun getPoetBios(): List<PoetBio> = withContext(Dispatchers.IO) {
        val jsonString = try {
            context.assets.open("poet_bios.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
        if (jsonString == null) return@withContext emptyList()
        val listType = object : TypeToken<List<PoetBio>>() {}.type
        try {
            Gson().fromJson(jsonString, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Playlist methods
    fun getAllPlaylists() = playlistDao.getAllPlaylists()
    
    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(com.zygisk.kavya_kanaja.data.local.PlaylistEntity(name = name))
    }
    
    suspend fun deletePlaylist(playlist: com.zygisk.kavya_kanaja.data.local.PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }
    
    suspend fun renamePlaylist(playlist: com.zygisk.kavya_kanaja.data.local.PlaylistEntity, newName: String) {
        playlistDao.updatePlaylist(playlist.copy(name = newName))
    }
    
    suspend fun addPoemToPlaylist(playlistId: Int, poemId: Int) {
        playlistDao.addPoemToPlaylist(com.zygisk.kavya_kanaja.data.local.PlaylistPoemCrossRef(playlistId, poemId))
    }
    
    suspend fun removePoemFromPlaylist(playlistId: Int, poemId: Int) {
        playlistDao.removePoemFromPlaylist(com.zygisk.kavya_kanaja.data.local.PlaylistPoemCrossRef(playlistId, poemId))
    }
    
    fun getPoemIdsInPlaylist(playlistId: Int) = playlistDao.getPoemIdsInPlaylist(playlistId)
    
    suspend fun ensureDefaultPlaylist(poemIds: List<Int>) {
        val name = "Local Hits"
        if (playlistDao.getPlaylistCountByName(name) == 0) {
            val id = createPlaylist(name).toInt()
            poemIds.forEach { addPoemToPlaylist(id, it) }
        }
    }

    suspend fun getAllPoems(): List<Poem> = withContext(Dispatchers.IO) {
        val jsonString = try {
            context.assets.open("poems.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }

        if (jsonString == null) {
            return@withContext listOf(
                Poem(
                    id = 0,
                    title = "Error Loading Poems",
                    poet = "System",
                    poetId = 0,
                    theme = "Error",
                    content = "Could not load poems from assets. Please check poems.json.",
                    meanings = emptyMap(),
                    audioFileName = null
                )
            )
        }

        val listType = object : TypeToken<List<Poem>>() {}.type
        try {
            Gson().fromJson(jsonString, listType) ?: emptyList()
        } catch (e: Exception) {
            listOf(
                Poem(
                    id = 0,
                    title = "Error Parsing Poems",
                    poet = "System",
                    poetId = 0,
                    theme = "Error",
                    content = "JSON structure might have changed. Please contact developer.",
                    meanings = emptyMap(),
                    audioFileName = null
                )
            )
        }
    }

    suspend fun getPoemById(id: Int): Poem? {
        val poems = getAllPoems()
        return poems.find { it.id == id }
    }
    
    fun getFavoritePoemsFlow() = poemDao.getFavoritePoems()
    
    fun getRecentlyPlayedFlow(limit: Int) = poemDao.getRecentlyPlayed(limit)
    
    suspend fun markAsPlayed(id: Int) {
        val currentProgress = poemDao.getProgressForPoemSync(id)
        if (currentProgress == null) {
            poemDao.insertOrUpdateProgress(
                com.zygisk.kavya_kanaja.data.local.PoemProgressEntity(
                    poemId = id,
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            poemDao.insertOrUpdateProgress(
                currentProgress.copy(lastReadTimestamp = System.currentTimeMillis())
            )
        }
    }

    suspend fun updatePlaybackPosition(id: Int, position: Int) {
        val currentProgress = poemDao.getProgressForPoemSync(id)
        if (currentProgress == null) {
            poemDao.insertOrUpdateProgress(
                com.zygisk.kavya_kanaja.data.local.PoemProgressEntity(
                    poemId = id,
                    lastAudioPosition = position,
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            poemDao.insertOrUpdateProgress(
                currentProgress.copy(
                    lastAudioPosition = position,
                    lastReadTimestamp = System.currentTimeMillis()
                )
            )
        }
    }
    
    fun getProgressForPoem(id: Int) = poemDao.getProgressForPoem(id)
    
    suspend fun getProgressForPoemSync(id: Int) = poemDao.getProgressForPoemSync(id)
    
    suspend fun toggleFavorite(id: Int, isCurrentlyFavorite: Boolean) {
        val currentProgress = poemDao.getProgressForPoemSync(id)
        if (currentProgress == null) {
            poemDao.insertOrUpdateProgress(
                com.zygisk.kavya_kanaja.data.local.PoemProgressEntity(
                    poemId = id,
                    isFavorite = !isCurrentlyFavorite
                )
            )
        } else {
            poemDao.updateFavoriteStatus(id, !isCurrentlyFavorite)
        }
    }
}