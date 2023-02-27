package com.ignitetech.compose.ui.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.data.call.CallWithTarget
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    callRepository: CallRepository
) : ViewModel() {
    private val groupDateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, HH mm")
    private val timeZone = TimeZone.currentSystemDefault()

    private val _calls = callRepository.getCalls().map { calls ->
        calls.map { it.call.date.toLocalDateTime(timeZone).date to it }
            .groupBy { it.first }
            .map { it.key to it.value.map { callMap -> callMap.second } }
            .associate {
                groupDateFormatter.format(it.first.toJavaLocalDate()) to it.second.map { call ->
                    call.asDetail(dateFormatter, timeZone)
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
    dateFormatter: DateTimeFormatter,
    timeZone: TimeZone
) = CallUiState.CallDetail(
    call.id,
    call.duration,
    call.type,
    dateFormatter.format(call.date.toLocalDateTime(timeZone).toJavaLocalDateTime()),
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