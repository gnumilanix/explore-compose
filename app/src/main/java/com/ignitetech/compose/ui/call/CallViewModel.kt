package com.ignitetech.compose.ui.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.data.call.CallWithTarget
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.domain.FormatInstantUseCase
import com.ignitetech.compose.domain.FormatLocalDateUseCase
import com.ignitetech.compose.domain.InstantToLocalDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    callRepository: CallRepository,
    formatInstantUseCase: FormatInstantUseCase,
    formatLocalDateUseCase: FormatLocalDateUseCase,
    instantToLocalDateUseCase: InstantToLocalDateUseCase
) : ViewModel() {
    private val _calls = callRepository.getCalls().map { calls ->
        calls.map { instantToLocalDateUseCase(it.call.date) to it }
            .groupBy { it.first }
            .map { it.key to it.value.map { callMap -> callMap.second } }
            .associate {
                formatLocalDateUseCase("MMMM dd", it.first) to it.second.map { call ->
                    call.asDetail(formatInstantUseCase)
                }
            }
    }

    var state = _calls.map { calls ->
        CallUiState(calls)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = CallUiState()
    )
}

private fun CallWithTarget.asDetail(
    formatInstantUseCase: FormatInstantUseCase
) = CallUiState.CallDetail(
    call.id!!,
    call.duration,
    call.type,
    formatInstantUseCase("MMMM dd, HH:mm", call.date),
    target
)

data class CallUiState(
    val calls: Map<String, List<CallDetail>> = mapOf()
) {
    data class CallDetail(
        val id: Int,
        val duration: Int,
        val type: Type,
        val date: String,
        val target: User
    )
}