package com.worldproger.mango.app.main.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.worldproger.mango.domain.model.UserModel
import com.worldproger.mango.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Chat(
    val id: String,
    val title: String,
    val lastMessage: String,
    val avatarUrl: String?
)

class ChatsViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    fun updateData() {
        loadCurrentUser()
        loadChats()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = userRepository.getUserCache()

            _currentUser.value = user.getOrNull()
        }
    }

    private fun loadChats() {
        _chats.value = listOf(
            Chat(
                id = "chat1",
                title = "Алексей",
                lastMessage = "Привет, как дела?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat2",
                title = "Мария",
                lastMessage = "Нужно завершить проект",
                avatarUrl = null,
            ),
            Chat(id = "chat3", title = "Иван", lastMessage = "Когда приедешь?", avatarUrl = null,),
            Chat(
                id = "chat4",
                title = "Ольга",
                lastMessage = "Идем на тренировку?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat5",
                title = "Дмитрий",
                lastMessage = "Куда поедем в отпуск?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat6",
                title = "Екатерина",
                lastMessage = "Слушал новый альбом?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat7",
                title = "Сергей",
                lastMessage = "Что читаешь сейчас?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat8",
                title = "Анна",
                lastMessage = "Смотрел новый фильм?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat9",
                title = "Николай",
                lastMessage = "Во что играешь?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat10",
                title = "Татьяна",
                lastMessage = "Какой твой любимый рецепт?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat11",
                title = "Владимир",
                lastMessage = "Как самочувствие?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat12",
                title = "Юлия",
                lastMessage = "Видел новую гаджет?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat13",
                title = "Андрей",
                lastMessage = "Читал последние новости?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat14",
                title = "Елена",
                lastMessage = "Чем увлекаешься?",
                avatarUrl = null,
            ),
            Chat(
                id = "chat15",
                title = "Игорь",
                lastMessage = "Как учеба?",
                avatarUrl = null,
            )
        )
    }
}