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

    suspend fun loadArticles(skip: Int = 0, limit: Int = 20): List<Article> {
        return try {
            // 呼叫本地 API
            com.example.devradarapp.network.RetrofitClient.instance.getArticles(skip, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            // 無法連線時回退到空列表或假資料
            emptyList()
        }
    }

    // 收藏操作
    fun getUserFavorites(userId: Int): Flow<List<FavoriteEntity>> = favoriteDao.getUserFavorites(userId)

    suspend fun addFavorite(favorite: FavoriteEntity) = favoriteDao.addFavorite(favorite)

    suspend fun removeFavorite(userId: Int, articleUrl: String) = favoriteDao.removeFavorite(userId, articleUrl)

    // 留言操作 (網路)
    suspend fun getComments(articleUrl: String): List<com.example.devradarapp.model.Comment> {
        return try {
            com.example.devradarapp.network.RetrofitClient.instance.getComments(articleUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 通知 (網路)
    suspend fun getUnreadNotifications(userId: Int): List<com.example.devradarapp.model.Notification> {
        return try {
            com.example.devradarapp.network.RetrofitClient.instance.getNotifications(userId).filter { !it.isRead }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun markNotificationsAsRead(userId: Int) {
         // 標記通知為已讀 (未實作詳細邏輯)
    }
    
    suspend fun markNotificationRead(notificationId: String) {
        try {
            com.example.devradarapp.network.RetrofitClient.instance.markNotificationRead(notificationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addComment(articleUrl: String, comment: com.example.devradarapp.model.Comment) {
        try {
            com.example.devradarapp.network.RetrofitClient.instance.addComment(comment)
            
            // 客戶端可以選擇樂觀更新列表
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun askAi(prompt: String, content: String): String {
        return try {
            val response = com.example.devradarapp.network.RetrofitClient.instance.askAi(
                com.example.devradarapp.network.AiRequest(prompt, content)
            )
            response.reply
        } catch (e: Exception) {
            e.printStackTrace()
            "AI Service Unavailable: ${e.message}"
        }
    }
}
