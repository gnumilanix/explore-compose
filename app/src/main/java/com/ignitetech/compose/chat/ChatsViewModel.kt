package com.ignitetech.compose.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.chat.ChatWithSender
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    chatRepository: ChatRepository
) : ViewModel() {
    private val _chats = MutableStateFlow(listOf<ChatUiState>())
    val chats = _chats.asStateFlow()

    init {
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
        val timeZone = TimeZone.currentSystemDefault()
        viewModelScope.launch {
            chatRepository.getLatestChats().collect { chats ->
                _chats.update {
                    chats.map {
                        it.toUiState(dateFormatter, timeZone)
                    }
                }
            }
        }
    }
}

fun ChatWithSender.toUiState(
    dateFormatter: DateTimeFormatter,
    timeZone: TimeZone
) = ChatUiState(
    chat.id,
    chat.userId,
    chat.message,
    chat.direction,
    dateFormatter.format(chat.date.toLocalDateTime(timeZone).toJavaLocalDateTime()),
    sender
)

data class ChatUiState(
    val id: Int,
    val userId: Int,
    val message: String,
    val direction: Direction,
    val date: String,
    val sender: User? = null,
)