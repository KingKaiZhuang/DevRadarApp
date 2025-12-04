package com.example.devradarapp

import OnboardingScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.devradarapp.ui.ExploreScreen
import com.example.devradarapp.ui.ProfileScreen
import com.example.devradarapp.ui.theme.DevRadarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevRadarAppTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    // 已移除 GoogleAuthManager 的初始化

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        // --------------------------
        // Onboarding
        // --------------------------
        composable("onboarding") {
            OnboardingScreen(
                onGoogleClick = {
                    // Google 登入邏輯已移除
                    Toast.makeText(context, "Google 登入功能已移除", Toast.LENGTH_SHORT).show()
                },
                onGithubClick = {
                    Toast.makeText(context, "Github Login 尚未實作", Toast.LENGTH_SHORT).show()
                },
                onGuestClick = {
                    navController.navigate("explore") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // --------------------------
        // Explore Page
        // --------------------------
        composable("explore") {
            ExploreScreen(
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        // --------------------------
        // Profile Page
        // --------------------------
        composable("profile") {
            ProfileScreen(
                onClose = {
                    navController.popBackStack()
                },
                onLogout = {
                    // Google 登出邏輯已移除，直接導回歡迎頁
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}