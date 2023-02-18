package com.ignitetech.compose.conversation

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.data.conversation.Conversation
import com.ignitetech.compose.data.conversation.ConversationRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ConversationViewModel @Inject constructor(
    userRepository: UserRepository,
    conversationRepository: ConversationRepository
) : ViewModel() {
    private val _user = MutableSharedFlow<User>()
    val user = _user.asSharedFlow()

    private val _conversations = MutableStateFlow(mapOf<String, List<Conversation>>())
    val conversation = _conversations.asStateFlow()

    init {
        _user.tryEmit(userRepository.getMe())
        _conversations.update { conversationRepository.getConversations(1) }
    }
}