package com.worldproger.mango.app.main.profile_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldproger.mango.domain.core.Result
import com.worldproger.mango.domain.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileEditState(
    val editableUser: EditableUser = EditableUser(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class EditableUser(
    var name: String = "",
    var username: String? = null,
    var birthday: String? = null,
    var city: String? = null,
    var vk: String? = null,
    var instagram: String? = null,
    var status: String? = null,
    var newAvatar: AvatarData? = null,
    val oldAvatar: String? = null
)

data class AvatarData(
    val filename: String,
    val base64: String
)

sealed class ProfileEditIntent {
    data class UpdateName(val name: String) : ProfileEditIntent()
    data class UpdateBirthday(val birthday: String) : ProfileEditIntent()
    data class UpdateCity(val city: String) : ProfileEditIntent()
    data class UpdateVk(val vk: String) : ProfileEditIntent()
    data class UpdateInstagram(val instagram: String) : ProfileEditIntent()
    data class UpdateStatus(val status: String) : ProfileEditIntent()
    data class UpdateAvatar(val avatarData: AvatarData) : ProfileEditIntent()
    data object SaveChanges : ProfileEditIntent()
    data object CancelEditing : ProfileEditIntent()
}

sealed class ProfileEditSideEffect {
    data class ShowError(val message: String) : ProfileEditSideEffect()
    data object NavigateBack : ProfileEditSideEffect()
}

class ProfileEditViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileEditState())
    val state: StateFlow<ProfileEditState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileEditSideEffect>()
    val sideEffect: SharedFlow<ProfileEditSideEffect> = _sideEffect.asSharedFlow()

    private val intentChannel = Channel<ProfileEditIntent>(Channel.UNLIMITED)

    init {
        handleIntents()
        loadUserProfile()
    }

    fun processIntent(intent: ProfileEditIntent) {
        intentChannel.trySend(intent)
    }

    private fun handleIntents() {
        viewModelScope.launch {
            for (intent in intentChannel) {
                when (intent) {
                    is ProfileEditIntent.UpdateName -> updateEditableUser { it.copy(name = intent.name) }
                    is ProfileEditIntent.UpdateBirthday -> updateEditableUser { it.copy(birthday = intent.birthday) }
                    is ProfileEditIntent.UpdateCity -> updateEditableUser { it.copy(city = intent.city) }
                    is ProfileEditIntent.UpdateVk -> updateEditableUser { it.copy(vk = intent.vk) }
                    is ProfileEditIntent.UpdateInstagram -> updateEditableUser { it.copy(instagram = intent.instagram) }
                    is ProfileEditIntent.UpdateStatus -> updateEditableUser { it.copy(status = intent.status) }
                    is ProfileEditIntent.UpdateAvatar -> updateEditableUser { it.copy(newAvatar = intent.avatarData) }
                    is ProfileEditIntent.SaveChanges -> saveChanges()
                    is ProfileEditIntent.CancelEditing -> _sideEffect.emit(ProfileEditSideEffect.NavigateBack)
                }
            }
        }
    }

    private fun updateEditableUser(update: (EditableUser) -> EditableUser) {
        _state.value = _state.value.copy(editableUser = update(_state.value.editableUser))
    }

    private suspend fun saveChanges() {
        _state.value = _state.value.copy(isLoading = true)
        val editableUser = _state.value.editableUser
        val result = userRepository.updateUser(
            name = editableUser.name,
            username = editableUser.username,
            birthday = editableUser.birthday,
            city = editableUser.city,
            instagram = editableUser.instagram,
            vk = editableUser.vk,
            status = editableUser.status,
            avatar = editableUser.newAvatar,
        )
        _state.value = _state.value.copy(isLoading = false)

        when (result) {
            is Result.Success -> _sideEffect.emit(ProfileEditSideEffect.NavigateBack)
            is Result.Error -> _sideEffect.emit(
                ProfileEditSideEffect.ShowError(result.error.message ?: "Ошибка сохранения изменений")
            )
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = userRepository.getUser()) {
                is Result.Success -> {
                    val user = result.data
                    _state.value = _state.value.copy(
                        editableUser = EditableUser(
                            name = user.name,
                            username = user.username,
                            birthday = user.birthday,
                            city = user.city,
                            vk = user.vk,
                            instagram = user.instagram,
                            status = user.status,
                            oldAvatar = user.avatars?.bigAvatar
                        ),
                        isLoading = false
                    )
                }
                is Result.Error -> _sideEffect.emit(
                    ProfileEditSideEffect.ShowError(result.error.message ?: "Ошибка загрузки профиля")
                )
            }
        }
    }
}