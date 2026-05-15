package com.zygisk.kavya_kanaja.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemDao {
    @Query("SELECT * FROM user_progress WHERE poemId = :poemId")
    fun getProgressForPoem(poemId: Int): Flow<PoemProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE isFavorite = 1")
    fun getFavoritePoems(): Flow<List<PoemProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE poemId = :poemId")
    suspend fun getProgressForPoemSync(poemId: Int): PoemProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: PoemProgressEntity)

    @Query("UPDATE user_progress SET isFavorite = :isFavorite WHERE poemId = :poemId")
    suspend fun updateFavoriteStatus(poemId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM user_progress ORDER BY lastReadTimestamp DESC LIMIT :limit")
    fun getRecentlyPlayed(limit: Int): Flow<List<PoemProgressEntity>>
}