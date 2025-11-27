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
import com.example.devradarapp.ui.ExploreScreen
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
        // --------------------------
        // Onboarding
        // --------------------------
        composable("onboarding") {
            OnboardingScreen(
                onGoogleClick = { /* TODO */ },
                onGithubClick = { /* TODO */ },
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
            ExploreScreen()
        }
    }
}
