package com.ignitetech.compose.call

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    callRepository: CallRepository
) : ViewModel() {
    private val _calls = MutableStateFlow(mapOf<String, List<CallUiState>>())
    val calls = _calls.asStateFlow()

    init {
        val callDateFormat = SimpleDateFormat("MMMM dd, HH mm", Locale.getDefault())
        _calls.update {
            callRepository.getCalls().map {
                it.key to it.value.map { call ->
                    CallUiState(
                        call.id,
                        call.userId,
                        call.duration,
                        call.type,
                        callDateFormat.format(call.date.time),
                        call.caller
                    )
                }
            }.toMap()
        }
    }
}

data class CallUiState(
    val id: Int,
    val userId: Int,
    val duration: Int,
    val type: Type,
    val date: String,
    val caller: User? = null,
)