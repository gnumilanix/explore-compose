package com.ignitetech.compose.chat

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    chatRepository: ChatRepository
) : ViewModel() {
    private val _chats = MutableStateFlow(listOf<ChatUiState>())
    val chats = _chats.asStateFlow()

    init {
        val callDateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        _chats.update {
            chatRepository.getChats().map { chat ->
                ChatUiState(
                    chat.id,
                    chat.userId,
                    chat.message,
                    chat.direction,
                    callDateFormat.format(chat.date.time),
                    chat.sender
                )
            }
        }
    }
}

data class ChatUiState(
    val id: Int,
    val userId: Int,
    val message: String,
    val direction: Direction,
    val date: String,
    val sender: User? = null,
)