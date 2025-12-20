package com.example.devradarapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.devradarapp.model.FavoriteEntity
import com.example.devradarapp.model.UserEntity

@Database(entities = [UserEntity::class, FavoriteEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "devradar_database"
                )
                    .fallbackToDestructiveMigration() // 開發階段允許資料庫重建
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}