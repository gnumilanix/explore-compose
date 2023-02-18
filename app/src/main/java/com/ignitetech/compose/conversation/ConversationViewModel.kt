package com.ignitetech.compose.conversation

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.SampleData
import kotlinx.coroutines.flow.*

class ConversationViewModel : ViewModel() {
    private val _user = MutableSharedFlow<User>()
    val user = _user.asSharedFlow()

    private val _conversations = MutableStateFlow(mapOf<String, List<Conversation>>())
    val conversation = _conversations.asStateFlow()

    init {
        _user.tryEmit(SampleData.userJack)
        _conversations.update { SampleData.messages }
    }
}