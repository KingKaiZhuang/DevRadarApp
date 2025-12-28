package com.example.devradarapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.devradarapp.data.AppDatabase
import com.example.devradarapp.data.ArticleRepository
import com.example.devradarapp.model.Article
import com.example.devradarapp.model.FavoriteEntity
import com.example.devradarapp.model.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ArticleRepository
    private val webSocketManager = com.example.devradarapp.network.WebSocketManager()
    private var currentUserId: Int? = null
    // 訪客 ID：如果未登入，使用負數隨機整數
    private val guestId: Int by lazy {
        -(java.util.Random().nextInt(1000000) + 1)
    }
    // 追蹤使用者目前正在查看的文章
    private var currentViewingArticleUrl: String? = null

    // 1. 儲存當前使用者的收藏文章 URL 集合 (用於快速判斷是否已收藏，例如顯示愛心顏色)
    private val _favoriteUrls = MutableStateFlow<Set<String>>(emptySet())
    val favoriteUrls: StateFlow<Set<String>> = _favoriteUrls.asStateFlow()

    // 2. 新增：完整的收藏列表 (用於收藏清單頁面顯示)
    private val _favoritesList = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favoritesList: StateFlow<List<FavoriteEntity>> = _favoritesList.asStateFlow()

    // 3. 文章列表
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    // 分頁狀態
    private var currentSkip = 0
    private val pageSize = 20
    private var isEndOfList = false

    private var isLoadingMore = false

    // AI 助手狀態
    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ArticleRepository(database.favoriteDao(), application)
        loadArticles(reset = true)
    }

    fun refreshArticles() {
        loadArticles(reset = true)
    }

    fun loadNextPage() {
        if (!isLoadingMore && !isEndOfList) {
            loadArticles(reset = false)
        }
    }

    private fun loadArticles(reset: Boolean) {
        if (isLoadingMore) return
        isLoadingMore = true
        
        if (reset) {
            currentSkip = 0
            isEndOfList = false
        }

        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val isIThome = prefs.getBoolean("source_ithome", true)
        val isThreads = prefs.getBoolean("source_threads", true)

        viewModelScope.launch {
            val newArticles = repository.loadArticles(skip = currentSkip, limit = pageSize)
            
            if (newArticles.isEmpty()) {
                isEndOfList = true
            } else {
                // 在本地端應用篩選
                // 注意：分頁會持續獲取原始頁面。
                // 如果我們進行過濾，顯示的項目可能會少於 pageSize。
                // 在理想情況下，我們應該獲取更多資料直到足夠為止。
                // 為了 MVP (最小可行性產品)，我們僅顯示符合的項目。
                
                val filtered = newArticles.filter { article ->
                    val src = article.source?.lowercase() ?: "unknown"
                    val allowIThome = isIThome && (src.contains("ithome") || src == "unknown") // 將未知來源視為 iThome/預設
                    val allowThreads = isThreads && (src.contains("threads") || src.contains("thread"))
                    
                    allowIThome || allowThreads
                }

                if (reset) {
                    _articles.value = filtered
                } else {
                    _articles.value = _articles.value + filtered
                }
                
                // 根據原始計數增加下一頁的 skip，以避免後端分頁中出現重複或缺漏
                currentSkip += newArticles.size
                
                // 最佳化：如果過濾結果為空但尚未到達列表末尾，是否自動載入下一頁？
                // 為了簡單起見並避免遞迴深度問題，此處省略，使用者可以滑動來觸發下一頁。
            }
            isLoadingMore = false
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

    // ---------------- 通知邏輯 ----------------
    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

    private val _notifications = MutableStateFlow<List<com.example.devradarapp.model.Notification>>(emptyList())
    val notifications: StateFlow<List<com.example.devradarapp.model.Notification>> = _notifications.asStateFlow()

    private val _newNotificationTrigger = MutableSharedFlow<Unit>()
    val newNotificationTrigger = _newNotificationTrigger.asSharedFlow()

    fun loadNotifications(userId: Int) {
        viewModelScope.launch {
            val notifs = repository.getUnreadNotifications(userId)
            _notifications.value = notifs
            _unreadNotificationCount.value = notifs.filter { !it.isRead }.size
        }
    }



    fun connectWebSocket(userId: Int?) {
        val idToConnect = userId ?: guestId
        
        // 如果切換使用者/訪客，先關閉先前的連線
        if (currentUserId != idToConnect) {
            webSocketManager.close()
        }
        
        currentUserId = idToConnect
        webSocketManager.connect(idToConnect)
        
        webSocketManager.onMessageReceived = { message ->
            if (message.contains("NOTIFICATION")) {
                // 重新整理通知 (訪客只會收到空列表，這沒問題)
                loadNotifications(idToConnect)
                viewModelScope.launch {
                    _newNotificationTrigger.emit(Unit)
                }
            } else if (message.contains("COMMENT_UPDATE")) {
                currentViewingArticleUrl?.let { url ->
                    if (message.contains(url)) {
                        loadComments(url)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.close()
    }
    
    fun markNotificationRead(notification: com.example.devradarapp.model.Notification) {
        viewModelScope.launch {
            repository.markNotificationRead(notification.id)
            // 本地更新以從列表中移除或標記為已讀
            loadNotifications(notification.userId)
        }
    }

    // ---------------- 留言邏輯 ----------------

    private val _currentComments = MutableStateFlow<List<com.example.devradarapp.model.Comment>>(emptyList())
    val currentComments: StateFlow<List<com.example.devradarapp.model.Comment>> = _currentComments.asStateFlow()

    fun loadComments(articleUrl: String) {
        currentViewingArticleUrl = articleUrl
        viewModelScope.launch {
            _currentComments.value = repository.getComments(articleUrl)
        }
    }

    fun clearCurrentComments() {
        currentViewingArticleUrl = null
        _currentComments.value = emptyList()
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
            // 重新載入以更新 UI
            loadComments(articleUrl)
            
            // 在真實應用中，我們可能會收到推播通知。
            // 這裡我們模擬立即檢查「另一個」使用者的通知
            // 但因為我們很可能是同一個使用者在測試，我們可以重新載入自己的通知看是否有收到 (自我回覆的情況)
            loadNotifications(user.id) 
        }
    }

    fun getUserIdForSession(user: UserEntity?): Int {
        return user?.id ?: guestId
    }

    fun askAi(prompt: String, articleContent: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiResponse.value = null // 清除先前的回應
            try {
                val response = repository.askAi(prompt, articleContent)
                _aiResponse.value = response
            } catch (e: Exception) {
                _aiResponse.value = "Error: ${e.message}"
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearAiResponse() {
        _aiResponse.value = null
    }
}
