package com.example.devradarapp.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// ------------------------------------
// 1. Entities
// ------------------------------------

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val initials: String
)

@Entity(
    tableName = "favorites",
    // 外鍵約束：當使用者被刪除時，其收藏也會被刪除 (CASCADE)
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    // 索引：確保同一個使用者對同一篇文章只能收藏一次，加快查詢速度
    indices = [Index(value = ["userId", "articleUrl"], unique = true)]
)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val articleUrl: String,
    val title: String, // 儲存標題以便離線顯示列表
    val author: String,
    val date: String
)

// ------------------------------------
// 2. DAOs
// ------------------------------------

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?
}

@Dao
interface FavoriteDao {
    // 取得某位使用者的所有收藏 (回傳 Flow 以便即時更新 UI)
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY id DESC")
    fun getUserFavorites(userId: Int): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND articleUrl = :articleUrl")
    suspend fun removeFavorite(userId: Int, articleUrl: String)
}

// ------------------------------------
// 3. Database Instance
// ------------------------------------

// 注意：Version 升級為 3，並加入 FavoriteEntity
@Database(entities = [UserEntity::class, FavoriteEntity::class], version = 3, exportSchema = false)
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