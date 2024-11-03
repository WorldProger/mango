package com.worldproger.mango.app.auth.phone_input

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

sealed class PhoneInputIntent {
    data class EnterPhoneNumber(val phoneNumber: String) : PhoneInputIntent()
    data class SendCode(val phone: String) : PhoneInputIntent()
}

data class PhoneInputState(
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class PhoneInputSideEffect {
    data class NavigateToCodeInput(val phoneNumber: String) : PhoneInputSideEffect()
    data class ShowError(val message: String) : PhoneInputSideEffect()
}

class PhoneInputViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PhoneInputState())
    val state: StateFlow<PhoneInputState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<PhoneInputSideEffect>()
    val sideEffect: SharedFlow<PhoneInputSideEffect> = _sideEffect.asSharedFlow()

    private val intentChannel = Channel<PhoneInputIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
    }

    fun processIntent(intent: PhoneInputIntent) {
        intentChannel.trySend(intent)
    }

    private fun handleIntents() {
        viewModelScope.launch {
            for (intent in intentChannel) {
                when (intent) {
                    is PhoneInputIntent.EnterPhoneNumber -> {
                        _state.value = _state.value.copy(phoneNumber = intent.phoneNumber)
                    }

                    is PhoneInputIntent.SendCode -> {
                        sendCode(phone = intent.phone)
                    }
                }
            }
        }
    }

    private suspend fun sendCode(phone: String) {
        if (phone.isBlank()) {
            _sideEffect.emit(PhoneInputSideEffect.ShowError("Введите номер телефона"))
            return
        }

        _state.value = _state.value.copy(isLoading = true)
        val result = authRepository.sendCode(phone)
        _state.value = _state.value.copy(isLoading = false)

        when (result) {
            is Result.Success -> {
                _sideEffect.emit(PhoneInputSideEffect.NavigateToCodeInput(phone))
            }

            is Result.Error -> {
                _sideEffect.emit(
                    PhoneInputSideEffect.ShowError(
                        result.error.message ?: "Ошибка отправки кода"
                    )
                )
            }
        }
    }
}