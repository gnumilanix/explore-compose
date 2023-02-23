package com.ignitetech.compose.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _users = MutableStateFlow(ChatUsersUiState())
    val users = _users.asStateFlow()

    private val _chats = MutableStateFlow(mapOf<String, List<ChatUiState>>())
    val chats = _chats.asStateFlow()

    init {
        viewModelScope.launch {
            val myId = preferenceRepository.userId()

            userRepository.getUsers(*listOfNotNull(myId, recipientId).toIntArray())
                .map { users ->
                    users.associateBy { it.id }
                }
                .collect {
                    _users.emit(ChatUsersUiState(it[myId], it[recipientId]))
                }
        }

        val groupDateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
        val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, HH mm")
        val timeZone = TimeZone.currentSystemDefault()

        viewModelScope.launch {
            chatRepository.getChats(recipientId).collect { calls ->
                _chats.update {
                    calls.map { it.chat.date.toLocalDateTime(timeZone).date to it }
                        .groupBy { it.first }
                        .map { it.key to it.value.map { callMap -> callMap.second } }
                        .associate {
                            groupDateFormatter.format(it.first.toJavaLocalDate()) to it.second.map { chat ->
                                chat.toUiState(dateFormatter, timeZone)
                            }
                        }
                }
            }
        }
    }

    companion object {
        const val RecipientId = "userId"
    }
}

data class ChatUsersUiState(
    val me: User? = null,
    val recipient: User? = null
)

