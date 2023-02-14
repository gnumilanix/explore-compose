package com.example.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _conversations = MutableStateFlow(mapOf<String, List<Conversation>>())
    val conversation: StateFlow<Map<String, List<Conversation>>> = _conversations.asStateFlow()


    init {
        _conversations.update { SampleData.messages }
    }
}