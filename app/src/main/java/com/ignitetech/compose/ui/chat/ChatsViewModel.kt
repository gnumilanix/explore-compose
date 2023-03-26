package com.ignitetech.compose.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.domain.ChatDetail
import com.ignitetech.compose.domain.FormatInstantUseCase
import com.ignitetech.compose.domain.asDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    chatRepository: ChatRepository,
    formatInstantUseCase: FormatInstantUseCase
) : ViewModel() {
    private val _chats = chatRepository.getLatestChats().map { chats ->
        chats.map {
            it.asDetail(formatInstantUseCase)
        }
    }

    var state = _chats.map { chats ->
        ChatsUiState(chats)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = ChatsUiState()
    )
}

data class ChatsUiState(
    val chats: List<ChatDetail> = listOf()
) {
}
