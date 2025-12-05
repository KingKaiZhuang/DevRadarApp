package com.example.devradarapp

import OnboardingScreen
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

                AppNavHost(
                    authViewModel = authViewModel,
                    articleViewModel = articleViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    articleViewModel: ArticleViewModel
) {
    val context = LocalContext.current

    // 觀察使用者狀態
    val currentUser by authViewModel.currentUser.collectAsState()

    // 觀察收藏數據
    val favoriteUrls by articleViewModel.favoriteUrls.collectAsState()
    val favoritesList by articleViewModel.favoritesList.collectAsState()

    // 當使用者狀態改變時 (例如登入成功)，載入該使用者的收藏
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            articleViewModel.loadFavorites(currentUser!!.id)

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
                onGoogleClick = { navController.navigate("login") },
                onGithubClick = { navController.navigate("login") },
                onGuestClick = {
                    authViewModel.logout()
                    navController.navigate("explore") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // --------------------------
        // Login
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
            ExploreScreen(
                favoriteUrls = favoriteUrls, // 傳入已收藏的 URL Set
                onProfileClick = { navController.navigate("profile") },
                onToggleFavorite = { article ->
                    if (currentUser != null) {
                        // 呼叫 ViewModel 切換收藏
                        articleViewModel.toggleFavorite(currentUser, article)
                    } else {
                        Toast.makeText(context, "請先登入才能收藏文章", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // --------------------------
        // Profile Page
        // --------------------------
        composable("profile") {
            ProfileScreen(
                currentUser = currentUser,
                onClose = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onFavoritesClick = {
                    // 導航至收藏頁面
                    navController.navigate("favorites")
                }
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
                }
            )
        }
    }
}