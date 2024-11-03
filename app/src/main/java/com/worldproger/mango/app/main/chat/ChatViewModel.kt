package com.worldproger.mango.app.main.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState

    init {
        loadChatData()
    }

    private fun loadChatData() {
        viewModelScope.launch {
            val fakeMessages = listOf(
                Message(id = 1, text = "Привет!", isMine = false),
                Message(id = 2, text = "Как дела?", isMine = false)
            )

            _chatState.value = _chatState.value.copy(
                messages = fakeMessages,
                isLoading = false
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val newMessage = Message(
            id = _chatState.value.messages.size + 1,
            text = text,
            isMine = true
        )

        _chatState.value = _chatState.value.copy(
            messages = _chatState.value.messages + newMessage,
            messageInput = ""
        )
    }

    fun onMessageInputChanged(text: String) {
        _chatState.value = _chatState.value.copy(messageInput = text)
    }
}

data class ChatState(
    val messages: List<Message> = emptyList(),
    val messageInput: String = "",
    val isLoading: Boolean = true
)

data class Message(
    val id: Int,
    val text: String,
    val isMine: Boolean
)
