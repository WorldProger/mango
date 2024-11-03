package com.worldproger.mango.app.auth.code_input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class CodeInputIntent {
    data class EnterCode(val code: String) : CodeInputIntent()
    data object VerifyCode : CodeInputIntent()
}

data class CodeInputState(
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class CodeInputSideEffect {
    data object NavigateToMain : CodeInputSideEffect()
    data object NavigateToRegistration : CodeInputSideEffect()
    data class ShowError(val message: String) : CodeInputSideEffect()
}

class CodeInputViewModel(
    private val phoneNumber: String,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CodeInputState())
    val state: StateFlow<CodeInputState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CodeInputSideEffect>()
    val sideEffect: SharedFlow<CodeInputSideEffect> = _sideEffect.asSharedFlow()

    private val intentChannel = Channel<CodeInputIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    fun processIntent(intent: CodeInputIntent) {
        intentChannel.trySend(intent)
    }

    private fun handleIntents() {
        viewModelScope.launch {
            for (intent in intentChannel) {
                when (intent) {
                    is CodeInputIntent.EnterCode -> {
                        _state.value = _state.value.copy(code = intent.code)
                    }

                    is CodeInputIntent.VerifyCode -> {
                        verifyCode()
                    }
                }
            }
        }
    }

    private suspend fun verifyCode() {
        val code = _state.value.code
        if (code.length != 6) {
            _sideEffect.emit(CodeInputSideEffect.ShowError("Введите 6-значный код"))
            return
        }

        _state.value = _state.value.copy(isLoading = true)
        val result = authRepository.verifyCode(phoneNumber, code)
        _state.value = _state.value.copy(isLoading = false)

        when (result) {
            is Result.Success -> {
                val isUserExists = result.data

                if (isUserExists) {
                    _sideEffect.emit(CodeInputSideEffect.NavigateToMain)
                } else {
                    _sideEffect.emit(CodeInputSideEffect.NavigateToRegistration)
                }
            }

            is Result.Error -> {
                _state.update { it.copy(errorMessage = it.errorMessage ?: "Неверный код") }
                delay(1000)
                _state.update { it.copy(errorMessage = null, code = "") }
            }
        }
    }
}