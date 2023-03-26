package com.ignitetech.compose.domain

import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.data.call.CallWithTarget
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCallByDateUseCase @Inject constructor(
    private val callRepository: CallRepository,
    private val formatInstantUseCase: FormatInstantUseCase,
    private val formatLocalDateUseCase: FormatLocalDateUseCase,
    private val instantToLocalDateUseCase: InstantToLocalDateUseCase
) {
    operator fun invoke(): Flow<List<CallsByDate>> {
        return callRepository.getCalls().map { calls ->
            calls.map { instantToLocalDateUseCase(it.call.date) to it }
                .groupBy { it.first }
                .map { it.key to it.value.map { callMap -> callMap.second } }
                .map {
                    CallsByDate(
                        formatLocalDateUseCase("MMMM dd", it.first),
                        it.second.map { call -> call.asDetail(formatInstantUseCase) }
                    )
                }
        }
    }
}

private fun CallWithTarget.asDetail(
    formatInstantUseCase: FormatInstantUseCase
) = CallDetail(
    call.id!!,
    call.duration,
    call.type,
    formatInstantUseCase("MMMM dd, HH:mm", call.date),
    target
)

data class CallDetail(
    val id: Int,
    val duration: Int,
    val type: Type,
    val date: String,
    val target: User
)

data class CallsByDate(
    val date: String,
    val calls: List<CallDetail>
)
