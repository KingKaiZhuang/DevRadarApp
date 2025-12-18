package com.example.devradarapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devradarapp.data.AppDatabase
import com.example.devradarapp.data.ArticleRepository
import com.example.devradarapp.model.Article
import com.example.devradarapp.model.FavoriteEntity
import com.example.devradarapp.model.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ArticleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ArticleRepository

    // 1. 儲存當前使用者的收藏文章 URL 集合 (用於快速判斷是否已收藏，例如顯示愛心顏色)
    private val _favoriteUrls = MutableStateFlow<Set<String>>(emptySet())
    val favoriteUrls: StateFlow<Set<String>> = _favoriteUrls.asStateFlow()

    // 2. 新增：完整的收藏列表 (用於收藏清單頁面顯示)
    private val _favoritesList = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favoritesList: StateFlow<List<FavoriteEntity>> = _favoritesList.asStateFlow()

    // 3. 文章列表
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ArticleRepository(database.favoriteDao(), application)
        loadArticles()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            _articles.value = repository.loadArticles()
        }
    }

    // 載入特定使用者的收藏
    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            repository.getUserFavorites(userId).collect { favorites ->
                // 更新 URL Set
                _favoriteUrls.value = favorites.map { it.articleUrl }.toSet()
                // 更新完整列表
                _favoritesList.value = favorites
            }
        }
    }

    // 清除狀態 (登出時使用)
    fun clearFavorites() {
        _favoriteUrls.value = emptySet()
        _favoritesList.value = emptyList()
    }

    // 切換收藏狀態
    fun toggleFavorite(user: UserEntity?, article: Article) {
        if (user == null) return // 未登入不能收藏

        viewModelScope.launch {
            val isFavorite = _favoriteUrls.value.contains(article.url)
            if (isFavorite) {
                // 移除收藏
                repository.removeFavorite(user.id, article.url)
            } else {
                // 加入收藏
                val newFav = FavoriteEntity(
                    userId = user.id,
                    articleUrl = article.url,
                    title = article.title,
                    author = article.author,
                    date = article.date,
                    category = article.category ?: "Uncategorized"
                )
                repository.addFavorite(newFav)
            }
        }
    }

    // 移除收藏 (從收藏列表頁面操作)
    fun removeFavorite(userId: Int, articleUrl: String) {
        viewModelScope.launch {
            repository.removeFavorite(userId, articleUrl)
        }
    }

    // ---------------- Notification Logic ----------------
    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

    private val _notifications = MutableStateFlow<List<com.example.devradarapp.model.Notification>>(emptyList())
    val notifications: StateFlow<List<com.example.devradarapp.model.Notification>> = _notifications.asStateFlow()

    fun loadNotifications(userId: Int) {
        viewModelScope.launch {
            val notifs = repository.getUnreadNotifications(userId)
            _notifications.value = notifs
            _unreadNotificationCount.value = notifs.filter { !it.isRead }.size
        }
    }

    suspend fun pollNotifications(userId: Int) {
        while (true) {
            loadNotifications(userId)
            delay(30000) // 30 seconds
        }
    }
    
    fun markNotificationRead(notification: com.example.devradarapp.model.Notification) {
        viewModelScope.launch {
            repository.markNotificationRead(notification.id)
            // Local update to remove from list or mark read
            loadNotifications(notification.userId)
        }
    }

    // ---------------- Comment Logic ----------------

    private val _currentComments = MutableStateFlow<List<com.example.devradarapp.model.Comment>>(emptyList())
    val currentComments: StateFlow<List<com.example.devradarapp.model.Comment>> = _currentComments.asStateFlow()

    fun loadComments(articleUrl: String) {
        viewModelScope.launch {
            _currentComments.value = repository.getComments(articleUrl)
        }
    }

    fun addComment(articleUrl: String, content: String, user: UserEntity, parentId: String? = null) {
        viewModelScope.launch {
            val newComment = com.example.devradarapp.model.Comment(
                id = java.util.UUID.randomUUID().toString(),
                articleUrl = articleUrl,
                userId = user.id,
                userName = user.name,
                content = content,
                timestamp = System.currentTimeMillis(),
                parentId = parentId
            )
            repository.addComment(articleUrl, newComment)
            // Reload to update UI
            loadComments(articleUrl)
            
            // In a real app, we might get a push notification. 
            // Here we simulate checking notifications immediately for the "other" user
            // But since we are likely the same user testing, we can reload our own notifications to see if we got one (self-reply case)
            loadNotifications(user.id) 
        }
    }
}
