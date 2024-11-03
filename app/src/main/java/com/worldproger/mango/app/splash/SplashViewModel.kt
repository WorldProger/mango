package com.worldproger.mango.app.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SplashState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed class SplashSideEffect {
    data object NavigateToMain : SplashSideEffect()
    data object NavigateToAuth : SplashSideEffect()
}

class SplashViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state

    private val _sideEffect = MutableSharedFlow<SplashSideEffect>()
    val sideEffect: SharedFlow<SplashSideEffect> = _sideEffect

    init {
        checkUser()
    }

    private fun checkUser() {
        viewModelScope.launch {
            _state.value = SplashState(isLoading = true)

            val result = userRepository.getUser()

            _state.value = SplashState(isLoading = false)

            when (result) {
                is Result.Success -> {
                    delay(1500)
                    _sideEffect.emit(SplashSideEffect.NavigateToMain)
                }

                is Result.Error -> {
                    delay(1500)
                    _sideEffect.emit(SplashSideEffect.NavigateToAuth)
                }
            }
        }
    }
}
