package com.ignitetech.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

class HomeViewModel : ViewModel() {
    private val _user = MutableSharedFlow<User>()
    val user = _user.asSharedFlow()
    private val _conversations = MutableStateFlow(mapOf<String, List<Conversation>>())
    val conversation = _conversations.asStateFlow()

    init {
        _user.tryEmit(SampleData.userJack)
        _conversations.update { SampleData.messages }
    }
}