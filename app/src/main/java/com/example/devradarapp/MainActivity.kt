package com.example.devradarapp

import OnboardingScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.devradarapp.ui.DailySummaryScreen
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
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGoogleClick = { /* TODO: Google Sign-In */ },
                onGithubClick = { /* TODO: Github Sign-In */ },
                onGuestClick = {
                    navController.navigate("daily") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("daily") {
            DailySummaryScreen()
        }
    }
}
