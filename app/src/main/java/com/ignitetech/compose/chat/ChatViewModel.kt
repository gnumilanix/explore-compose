package com.ignitetech.compose.chat

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.data.chat.Chat
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    userRepository: UserRepository,
    chatRepository: ChatRepository
) : ViewModel() {
    private val _user = MutableSharedFlow<User>()
    val user = _user.asSharedFlow()

    private val _conversations = MutableStateFlow(mapOf<String, List<Chat>>())
    val conversation = _conversations.asStateFlow()

    init {
        _user.tryEmit(userRepository.getMe())
        _conversations.update { chatRepository.getChats(1) }
    }
}