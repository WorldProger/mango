package com.worldproger.mango.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.worldproger.mango.app.auth.authGraph
import com.worldproger.mango.app.main.mainGraph
import com.worldproger.mango.app.splash.splashGraph
import com.worldproger.mango.app.theme.AppTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AppTheme {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}

@Serializable
sealed class Routes {
    @Serializable
    data object Splash : Routes()

    @Serializable
    data object PhoneInput : Routes()

    @Serializable
    data class CodeInput(val phone: String) : Routes()

    @Serializable
    data class Register(val phoneNumber: String) : Routes()

    @Serializable
    data object Chats : Routes()

    @Serializable
    data class Chat(val chatId: String) : Routes()

    @Serializable
    data object Profile : Routes()

    @Serializable
    data object EditProfile : Routes()
}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash,
    ) {
        splashGraph(navController)
        authGraph(navController)
        mainGraph(navController)
    }
}