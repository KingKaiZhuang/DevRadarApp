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
            // Call the local API
            com.example.devradarapp.network.RetrofitClient.instance.getArticles(skip, limit)
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

    // Comment Operations (Network)
    suspend fun getComments(articleUrl: String): List<com.example.devradarapp.model.Comment> {
        return try {
            com.example.devradarapp.network.RetrofitClient.instance.getComments(articleUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Notifications (Network)
    suspend fun getUnreadNotifications(userId: Int): List<com.example.devradarapp.model.Notification> {
        return try {
            com.example.devradarapp.network.RetrofitClient.instance.getNotifications(userId).filter { !it.isRead }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun markNotificationsAsRead(userId: Int) {
         // Identify unread ones first ideally, but for now we rely on UI to trigger read for specific ones or loop
         // In a real app we might have a bulk endpoint.
         // Here we will just fetch and mark all as read one by one or let UI handle it.
         // Actually, let's just expose a method to mark ONE as read.
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
            
            // Client-side notification logic (Optimistic / Local only for now)
            // Ideally this moves to backend or we fetch notifications from backend
             if (comment.parentId != null) {
                // We'd need to fetch parent comment or trust current context.
                // For now, simplify or temporarily disable local notification gen if we don't have parent loaded.
                // Or we can just leave it out as the user asked for COMMENT sync.
             }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
