package com.worldproger.mango.app.di

import com.worldproger.mango.app.auth.code_input.CodeInputViewModel
import com.worldproger.mango.app.auth.phone_input.PhoneInputViewModel
import com.worldproger.mango.app.auth.register.RegisterViewModel
import com.worldproger.mango.app.main.chat.ChatViewModel
import com.worldproger.mango.app.main.chats.ChatsViewModel
import com.worldproger.mango.app.main.profile.ProfileViewModel
import com.worldproger.mango.app.main.profile_edit.ProfileEditViewModel
import com.worldproger.mango.app.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    viewModel {
        PhoneInputViewModel(
            authRepository = get()
        )
    }

    viewModel { parameters ->
        CodeInputViewModel(
            phoneNumber = parameters.get<String>(),
            authRepository = get()
        )
    }

    viewModel { parameters ->
        RegisterViewModel(
            phoneNumber = parameters.get<String>(),
            authRepository = get()
        )
    }

    viewModel {
        SplashViewModel(
            userRepository = get()
        )
    }

    viewModel {
        ChatsViewModel(
            userRepository = get()
        )
    }

    viewModel {
        ChatViewModel()
    }

    viewModel {
        ProfileViewModel(get())
    }

    viewModel {
        ProfileEditViewModel(get())
    }
}