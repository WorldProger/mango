package com.worldproger.mango.app.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterIntent {
    data class EnterName(val name: String) : RegisterIntent()
    data class EnterUsername(val username: String) : RegisterIntent()
    data object Register : RegisterIntent()
}

data class RegisterState(
    val phoneNumber: String = "",
    val name: String = "",
    val username: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


sealed class RegisterSideEffect {
    data object NavigateToMain : RegisterSideEffect()
    data class ShowError(val message: String) : RegisterSideEffect()
}


class RegisterViewModel(
    private val phoneNumber: String,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState(phoneNumber = phoneNumber))
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<RegisterSideEffect>()
    val sideEffect: SharedFlow<RegisterSideEffect> = _sideEffect.asSharedFlow()

    private val intentChannel = Channel<RegisterIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    fun processIntent(intent: RegisterIntent) {
        intentChannel.trySend(intent)
    }

    private fun handleIntents() {
        viewModelScope.launch {
            for (intent in intentChannel) {
                when (intent) {
                    is RegisterIntent.EnterName -> {
                        _state.value = _state.value.copy(name = intent.name)
                    }

                    is RegisterIntent.EnterUsername -> {
                        _state.value = _state.value.copy(username = intent.username)
                    }

                    is RegisterIntent.Register -> {
                        registerUser()
                    }
                }
            }
        }
    }

    private suspend fun registerUser() {
        val name = _state.value.name.trim()
        val username = _state.value.username.trim()

        if (name.isBlank()) {
            _sideEffect.emit(RegisterSideEffect.ShowError("Введите имя"))
            return
        }

        if (!isUsernameValid(username)) {
            _sideEffect.emit(RegisterSideEffect.ShowError("Некорректный username"))
            return
        }

        _state.value = _state.value.copy(isLoading = true)
        val result = authRepository.register(phone = phoneNumber, name = name, username = username)
        _state.value = _state.value.copy(isLoading = false)

        when (result) {
            is Result.Success -> {
                _sideEffect.emit(RegisterSideEffect.NavigateToMain)
            }

            is Result.Error -> {
                _sideEffect.emit(
                    RegisterSideEffect.ShowError(
                        result.error.message ?: "Ошибка регистрации"
                    )
                )
            }
        }
    }

    private fun isUsernameValid(username: String): Boolean {
        val regex = Regex("^[A-Za-z0-9-_]+$")
        return regex.matches(username)
    }
}
