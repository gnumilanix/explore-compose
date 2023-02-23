package com.ignitetech.compose.ui.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.data.call.CallWithTarget
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val _calls = MutableStateFlow(mapOf<String, List<CallUiState>>())
    val calls = _calls.asStateFlow()

    init {
        val groupDateFormatter = DateTimeFormatter.ofPattern("MMMM dd")
        val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, HH mm")
        val timeZone = TimeZone.currentSystemDefault()

        viewModelScope.launch {
            callRepository.getCalls().collect { calls ->
                _calls.update {
                    calls.map { it.call.date.toLocalDateTime(timeZone).date to it }
                        .groupBy { it.first }
                        .map { it.key to it.value.map { callMap -> callMap.second } }
                        .associate {
                            groupDateFormatter.format(it.first.toJavaLocalDate()) to it.second.map { call ->
                                call.toUiState(dateFormatter, timeZone)
                            }
                        }
                }
            }
        }
    }
}

private fun CallWithTarget.toUiState(
    dateFormatter: DateTimeFormatter,
    timeZone: TimeZone
) = CallUiState(
    call.id,
    call.duration,
    call.type,
    dateFormatter.format(
        call.date.toLocalDateTime(timeZone).toJavaLocalDateTime()
    ),
    target
)

data class CallUiState(
    val id: Int,
    val duration: Int,
    val type: Type,
    val date: String,
    val target: User
)