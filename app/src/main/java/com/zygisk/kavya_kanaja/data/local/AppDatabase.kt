package com.zygisk.kavya_kanaja.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PoemProgressEntity::class, 
        UserProfileEntity::class, 
        PlaylistEntity::class, 
        PlaylistPoemCrossRef::class
    ], 
    version = 4, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun poemDao(): PoemDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kavya_kanaja_db"
                )
                .fallbackToDestructiveMigration() // For development ease with schema changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
