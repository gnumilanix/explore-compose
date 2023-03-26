package com.ignitetech.compose.ui.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.domain.CallsByDate
import com.ignitetech.compose.domain.GetCallByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    getCallByDateUseCase: GetCallByDateUseCase
) : ViewModel() {
    private val _calls = getCallByDateUseCase()

    var state = _calls.map { calls ->
        CallUiState(calls)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = CallUiState()
    )
}

data class CallUiState(
    val calls: List<CallsByDate> = listOf()
)
