package com.example.devradarapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.devradarapp.model.FavoriteEntity
import kotlinx.coroutines.flow.Flow

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
