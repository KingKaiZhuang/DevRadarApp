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
    // Load articles from JSON
    fun loadArticles(): List<Article> {
        val fileName = "ithelp_hot.json"
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            return createDummyArticles()
        }
        return try {
            Json.decodeFromString<List<Article>>(jsonString)
        } catch (e: Exception) {
            createDummyArticles()
        }
    }

    private fun createDummyArticles(): List<Article> {
        return listOf(
            Article(
                title = "💳 用 n8n 將信用卡消費資料寫入 Google Sheets",
                desc = "這篇文章主要記錄如何用 n8n 把解析後的帳單資料自動寫入 Google Sheets...",
                url = "https://ithelp.ithome.com.tw/",
                author = "劉小貢", date = "2025-11-11",
                like = "1", comments = "0", views = "1663"
            ),
            Article(
                title = "【Compose】從零開始打造自訂主題和排版",
                desc = "深入探討 Material 3 的顏色系統、字體排版。",
                url = "https://google.com",
                author = "邦邦小幫手", date = "2025-11-15",
                like = "12", comments = "3", views = "2000"
            )
        )
    }

    // Favorite Operations
    fun getUserFavorites(userId: Int): Flow<List<FavoriteEntity>> = favoriteDao.getUserFavorites(userId)

    suspend fun addFavorite(favorite: FavoriteEntity) = favoriteDao.addFavorite(favorite)

    suspend fun removeFavorite(userId: Int, articleUrl: String) = favoriteDao.removeFavorite(userId, articleUrl)
}
