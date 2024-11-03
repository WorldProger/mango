package com.worldproger.mango.app.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.worldproger.mango.app.Routes
import com.worldproger.mango.app.main.chat.ChatScreen
import com.worldproger.mango.app.main.chats.ChatsScreen
import com.worldproger.mango.app.main.profile.ProfileScreen
import com.worldproger.mango.app.main.profile_edit.ProfileEditScreen

fun NavGraphBuilder.mainGraph(navController: NavController) {
    composable<Routes.Chats> {
        ChatsScreen(
            onNavigateToChat = { chatId ->
                navController.navigate(Routes.Chat(chatId))
            },
            onNavigateToProfile = {
                navController.navigate(Routes.Profile)
            }
        )
    }
    composable<Routes.Chat> { backStackEntry ->
        val chatId = backStackEntry.toRoute<Routes.Chat>().chatId

        ChatScreen(
            chatId = chatId,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable<Routes.Profile> {
        ProfileScreen(
            onNavigateBack = { navController.popBackStack() },
            onEditProfile = {
                navController.navigate(Routes.EditProfile)
            },
            onLogout = {
                navController.navigate(Routes.PhoneInput) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        )
    }
    composable<Routes.EditProfile> {
        ProfileEditScreen(
            onNavigateBack = { navController.popBackStack() },
        )
    }
}
