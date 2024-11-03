package com.worldproger.mango.app.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.model.UserModel
import com.worldproger.mango.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class ProfileState(
    val user: UserModel? = null,
    val zodiacSign: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

sealed class ProfileSideEffect {
    data class ShowError(val message: String) : ProfileSideEffect()
    data object Logout : ProfileSideEffect()
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileSideEffect>()
    val sideEffect: SharedFlow<ProfileSideEffect> = _sideEffect.asSharedFlow()

    fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = userRepository.getUser()
            _state.value = _state.value.copy(isLoading = false)
            when (result) {
                is Result.Success -> {
                    val user = result.data
                    val zodiacSign = calculateZodiacSign(user.birthday)
                    val formattedBirthday = user.birthday?.let { formatDateToReadable(it) }
                    _state.value = _state.value.copy(
                        user = user.copy(birthday = formattedBirthday),
                        zodiacSign = zodiacSign
                    )
                }
                is Result.Error -> {
                    _sideEffect.emit(
                        ProfileSideEffect.ShowError(
                            result.error.message ?: "Ошибка загрузки профиля"
                        )
                    )
                }
            }
        }
    }

    private fun formatDateToReadable(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val outputFormat = SimpleDateFormat("d MMMM yyyy года", Locale("ru"))
            outputFormat.format(date ?: return "")
        } catch (e: Exception) {
            ""
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _sideEffect.emit(ProfileSideEffect.Logout)
        }
    }

    private fun calculateZodiacSign(dateOfBirth: String?): String {
        if (dateOfBirth == null) return ""
        val (_, month, day) = dateOfBirth.split("-").map { it.toInt() }
        return getZodiacSign(month, day)
    }

    private fun getZodiacSign(month: Int, day: Int): String {
        return when {
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей"
            (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "Рыбы"
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен"
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец"
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы"
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак"
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев"
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева"
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы"
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион"
            (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Стрелец"
            else -> "Козерог"
        }
    }
}