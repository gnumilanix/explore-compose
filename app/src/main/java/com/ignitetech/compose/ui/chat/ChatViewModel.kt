package com.ignitetech.compose.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import com.ignitetech.compose.domain.ChatsByDate
import com.ignitetech.compose.domain.GetChatByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    userRepository: UserRepository,
    getChatByDateUseCase: GetChatByDateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val recipientId = savedStateHandle.get<Int>(RecipientId)!!

    private val _me = userRepository.getMe()
    private val _recipient = userRepository.getUser(recipientId)
    private val _chats = getChatByDateUseCase(recipientId)

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
    val chats: List<ChatsByDate> = listOf()
)
