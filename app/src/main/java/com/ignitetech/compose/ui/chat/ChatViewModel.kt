package com.ignitetech.compose.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    userRepository: UserRepository,
    chatRepository: ChatRepository,
    preferenceRepository: PreferenceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val recipientId = savedStateHandle.get<Int>(RecipientId)!!

    private val groupDateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, HH mm")
    private val timeZone = TimeZone.currentSystemDefault()

    private val _me = preferenceRepository.userIdFlow.map {
        when (it) {
            null -> null
            else -> userRepository.getUser(it).first()
        }
    }
    private val _recipient = userRepository.getUser(recipientId)
    private val _chats = chatRepository.getChats(recipientId).map { calls ->
        calls.map { it.chat.date.toLocalDateTime(timeZone).date to it }
            .groupBy { it.first }
            .map { it.key to it.value.map { callMap -> callMap.second } }
            .associate {
                groupDateFormatter.format(it.first.toJavaLocalDate()) to it.second.map { chat ->
                    chat.asDetail(dateFormatter, timeZone)
                }
            }
    }

    var state = combine(_me, _recipient, _chats) { me, recipient, chats ->
        ChatUiState(me, recipient, chats)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = ChatUiState()
    )

    companion object {
        const val RecipientId = "userId"
    }
}

data class ChatUiState(
    val me: User? = null,
    val recipient: User? = null,
    val chats: Map<String, List<ChatsUiState.ChatDetail>> = mapOf()
)

