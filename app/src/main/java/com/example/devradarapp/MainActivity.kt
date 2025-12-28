package com.example.devradarapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.devradarapp.ui.ExploreScreen
import com.example.devradarapp.ui.FavoritesScreen
import com.example.devradarapp.ui.LoginScreen
import com.example.devradarapp.ui.OnboardingScreen
import com.example.devradarapp.ui.ProfileScreen
import com.example.devradarapp.ui.theme.DevRadarAppTheme
import com.example.devradarapp.viewmodel.ArticleViewModel
import com.example.devradarapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevRadarAppTheme {
                // 初始化 ViewModels
                val authViewModel: AuthViewModel = viewModel()
                val articleViewModel: ArticleViewModel = viewModel()
                val trendViewModel: com.example.devradarapp.viewmodel.TrendViewModel = viewModel()

                AppNavHost(
                    authViewModel = authViewModel,
                    articleViewModel = articleViewModel,
                    trendViewModel = trendViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    articleViewModel: ArticleViewModel,
    trendViewModel: com.example.devradarapp.viewmodel.TrendViewModel
) {
    val context = LocalContext.current

    // 觀察使用者狀態
    val currentUser by authViewModel.currentUser.collectAsState()

    // 觀察收藏數據
    val favoriteUrls by articleViewModel.favoriteUrls.collectAsState()
    val favoritesList by articleViewModel.favoritesList.collectAsState()
    
    // 觀察文章列表 (新增)
    val articles by articleViewModel.articles.collectAsState()

    // 當使用者狀態改變時 (例如登入成功)，載入該使用者的收藏
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            articleViewModel.loadFavorites(currentUser!!.id)
            articleViewModel.loadNotifications(currentUser!!.id)

            // 自動跳轉邏輯
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == "onboarding" || currentRoute == "login") {
                navController.navigate("explore") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        } else {
            // 登出時清空收藏
            articleViewModel.clearFavorites()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        // --------------------------
        // Onboarding
        // --------------------------
        composable("onboarding") {
            OnboardingScreen(
                onLoginClick = { navController.navigate("login") },
                onGuestClick = {
                    authViewModel.logout()
                    navController.navigate("explore") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // --------------------------
        // 登入
        // --------------------------
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { /* 導航已由上面的 LaunchedEffect 處理 */ }
            )
        }

        // --------------------------
        // Explore Page (整合收藏功能)
        // --------------------------
                composable("explore") {
                    // 通知狀態
                    val notificationCount by articleViewModel.unreadNotificationCount.collectAsState()
                    val notifications by articleViewModel.notifications.collectAsState()
                    
                    // 連接 WebSocket 進行即時更新 (已登入或訪客)
                    LaunchedEffect(currentUser) {
                        articleViewModel.connectWebSocket(currentUser?.id)
                    }
                    
                    // 顯示新通知的 Toast
                    val context = androidx.compose.ui.platform.LocalContext.current
                    LaunchedEffect(Unit) {
                        articleViewModel.newNotificationTrigger.collect {
                            android.widget.Toast.makeText(context, "New Reply Received! 🔔", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }

                    ExploreScreen(
                        articles = articles, // 傳入文章列表
                        favoriteUrls = favoriteUrls, // 傳入已收藏的 URL Set
                        onProfileClick = { navController.navigate("profile") },
                        onArticleClick = { url ->
                            val encodedUrl = java.net.URLEncoder.encode(url, java.nio.charset.StandardCharsets.UTF_8.toString())
                            navController.navigate("article_detail/$encodedUrl")
                        },
                        onToggleFavorite = { article ->
                            if (currentUser != null) {
                                // 呼叫 ViewModel 切換收藏
                                articleViewModel.toggleFavorite(currentUser, article)
                            } else {
                                Toast.makeText(context, "請先登入才能收藏文章", Toast.LENGTH_SHORT).show()
                            }
                        },
                        unreadNotificationCount = notificationCount,
                        notifications = notifications,
                        onNotificationClick = { notification ->
                            articleViewModel.markNotificationRead(notification)
                            // 如果 URL 存在，導航至文章
                            if (notification.articleUrl != null) {
                                val encodedUrl = java.net.URLEncoder.encode(notification.articleUrl, java.nio.charset.StandardCharsets.UTF_8.toString())
                                navController.navigate("article_detail/$encodedUrl")
                            }
                        },
                        onRefreshNotifications = {
                            currentUser?.let { articleViewModel.loadNotifications(it.id) }
                        },
                        onLoadMore = {
                            articleViewModel.loadNextPage()
                        }
                    )
                }

        // --------------------------
        // Profile Page
        // --------------------------
        composable("profile") {
            ProfileScreen(
                currentUser = currentUser,
                onClose = { 
                    articleViewModel.refreshArticles()
                    navController.popBackStack() 
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onFavoritesClick = {
                    // 導航至收藏頁面
                    navController.navigate("favorites")
                },
                onTrendsClick = {
                    navController.navigate("trends")
                }
            )
        }
        
        composable("trends") {
            val keywords by trendViewModel.keywords.collectAsState()
            val isLoading by trendViewModel.isLoading.collectAsState()
            
            com.example.devradarapp.ui.TrendScreen(
                keywords = keywords,
                isLoading = isLoading,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --------------------------
        // Favorites Page (新增)
        // --------------------------
        composable("favorites") {
            FavoritesScreen(
                favorites = favoritesList,
                onBackClick = {
                    navController.popBackStack()
                },
                onRemoveClick = { articleUrl ->
                    // 從收藏頁面移除項目
                    if (currentUser != null) {
                        articleViewModel.removeFavorite(currentUser!!.id, articleUrl)
                    }
                },
                onArticleClick = { url ->
                    val encodedUrl = java.net.URLEncoder.encode(url, java.nio.charset.StandardCharsets.UTF_8.toString())
                    navController.navigate("article_detail/$encodedUrl")
                }
            )
        }

        // --------------------------
        // Article Detail (新增)
        // --------------------------
        composable("article_detail/{articleUrl}") { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl") ?: ""
            // 如果傳遞了複雜的 URL，可能需要 URL 解碼
            val decodedUrl = java.net.URLDecoder.decode(articleUrl, java.nio.charset.StandardCharsets.UTF_8.toString())

            com.example.devradarapp.ui.ArticleDetailScreen(
                articleUrl = decodedUrl,
                viewModel = articleViewModel,
                currentUser = currentUser,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}