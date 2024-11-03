package com.worldproger.mango.app.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.worldproger.mango.app.Routes
import com.worldproger.mango.app.auth.code_input.CodeInputScreen
import com.worldproger.mango.app.auth.phone_input.PhoneInputScreen
import com.worldproger.mango.app.auth.register.RegisterScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable<Routes.PhoneInput> {
        PhoneInputScreen(
            onNavigateToCodeInput = { phone ->
                navController.navigate(Routes.CodeInput(phone))
            }
        )
    }

    composable<Routes.CodeInput> {
        val phone = it.toRoute<Routes.CodeInput>().phone

        CodeInputScreen(
            phoneNumber = phone,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToMain = {
                navController.navigate(Routes.Chats) {
                    popUpTo(Routes.PhoneInput) { inclusive = true }
                }
            },
            onNavigateToRegistration = {
                navController.navigate(Routes.Register(phone))
            }
        )
    }

    composable<Routes.Register> {
        val phone = it.toRoute<Routes.Register>().phoneNumber

        RegisterScreen(
            phoneNumber = phone,
            onNavigateBack = {
                navController.popBackStack(Routes.PhoneInput, inclusive = false)
            },
            onNavigateToMain = {
                navController.navigate(Routes.Chats) {
                    popUpTo(Routes.PhoneInput) { inclusive = true }
                }
            }
        )
    }
}
