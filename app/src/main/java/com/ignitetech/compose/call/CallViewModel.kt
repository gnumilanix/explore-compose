package com.ignitetech.compose.call

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.data.call.Call
import com.ignitetech.compose.data.call.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    callRepository: CallRepository
) : ViewModel() {
    private val _calls = MutableStateFlow(mapOf<String, List<Call>>())
    val calls = _calls.asStateFlow()

    init {
        _calls.update { callRepository.getCalls() }
    }
}