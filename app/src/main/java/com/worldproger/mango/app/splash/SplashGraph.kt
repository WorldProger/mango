package com.worldproger.mango.app.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.worldproger.mango.app.Routes

// Граф Splash
fun NavGraphBuilder.splashGraph(navController: NavController) {
    composable<Routes.Splash> {
        SplashScreen(
            onNavigateToAuth = {
                navController.navigate(Routes.PhoneInput) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            },
            onNavigateToMain = {
                navController.navigate(Routes.Chats) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        )
    }
}