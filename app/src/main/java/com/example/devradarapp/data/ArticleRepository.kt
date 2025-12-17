package com.example.devradarapp.data

import android.content.Context
import com.example.devradarapp.model.Article
import com.example.devradarapp.model.FavoriteEntity
import com.example.devradarapp.model.UserEntity
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class ArticleRepository(
    private val favoriteDao: FavoriteDao,
    private val context: Context
) {

    suspend fun loadArticles(): List<Article> {
        return try {
            // Call the local API
            com.example.devradarapp.network.RetrofitClient.instance.getArticles()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to empty list or dummy data if connection fails
            emptyList()
        }
    }

    // Favorite Operations
    fun getUserFavorites(userId: Int): Flow<List<FavoriteEntity>> = favoriteDao.getUserFavorites(userId)

    suspend fun addFavorite(favorite: FavoriteEntity) = favoriteDao.addFavorite(favorite)

    suspend fun removeFavorite(userId: Int, articleUrl: String) = favoriteDao.removeFavorite(userId, articleUrl)
}
