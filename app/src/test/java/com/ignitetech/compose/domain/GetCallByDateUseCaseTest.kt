package com.ignitetech.compose.domain

import app.cash.turbine.test
import com.ignitetech.compose.data.call.Call
import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.data.call.CallWithTarget
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCallByDateUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var callRepository: CallRepository

    @MockK
    lateinit var formatInstantUseCase: FormatInstantUseCase

    @MockK
    lateinit var formatLocalDateUseCase: FormatLocalDateUseCase

    @MockK
    lateinit var instantToLocalDateUseCase: InstantToLocalDateUseCase

    @InjectMockKs
    lateinit var getCallByDateUseCase: GetCallByDateUseCase

    @Test
    fun `invoke maps calls by date correctly`() = runTest {
        val groupDateFormat = "MMMM dd"
        val chatDateFormat = "MMMM dd, HH:mm"

        val user1 = User(1, "John", "https://placekitten.com/200/300")
        val user2 = User(2, "Jane", "https://placekitten.com/200/100")

        val localDate1 = mockk<LocalDate>()
        val localDate2 = mockk<LocalDate>()
        val localDateString1 = "January 01"
        val localDateString2 = "January 02"

        val dateString1 = "January 01, 00:15"
        val date1 = mockk<Instant>()
        val call1 = Call(0, 1, 60000, Type.OUTGOING, date1)
        val callWithTarget1 = CallWithTarget(call1, user1)

        val dateString2 = "January 01, 00:30"
        val date2 = mockk<Instant>()
        val call2 = Call(1, 2, 6000, Type.OUTGOING_MISSED, date2)
        val callWithTarget2 = CallWithTarget(call2, user2)

        val date3 = mockk<Instant>()
        val dateString3 = "January 02, 00:15"
        val call3 = Call(3, 1, 30000, Type.INCOMING, date3)
        val callWithTarget3 = CallWithTarget(call3, user1)

        val callByDates = listOf(callWithTarget1, callWithTarget2, callWithTarget3)
        val expectedFlow = listOf(
            CallsByDate(
                localDateString1,
                listOf(
                    CallDetail(
                        call1.id!!,
                        call1.duration,
                        call1.type,
                        dateString1,
                        user1
                    ),
                    CallDetail(
                        call2.id!!,
                        call2.duration,
                        call2.type,
                        dateString2,
                        user2
                    )
                )
            ),
            CallsByDate(
                localDateString2,
                listOf(
                    CallDetail(
                        call3.id!!,
                        call3.duration,
                        call3.type,
                        dateString3,
                        user1
                    )
                )
            )
        )

        every { instantToLocalDateUseCase(date1) } returns localDate1
        every { instantToLocalDateUseCase(date2) } returns localDate1
        every { instantToLocalDateUseCase(date3) } returns localDate2

        every { formatLocalDateUseCase(groupDateFormat, localDate1) } returns localDateString1
        every { formatLocalDateUseCase(groupDateFormat, localDate2) } returns localDateString2

        every { formatInstantUseCase(chatDateFormat, date1) } returns dateString1
        every { formatInstantUseCase(chatDateFormat, date2) } returns dateString2
        every { formatInstantUseCase(chatDateFormat, date3) } returns dateString3

        every { callRepository.getCalls() } returns flow {
            delay(100)
            emit(callByDates)
        }

        getCallByDateUseCase().test {
            assertEquals(expectedFlow, awaitItem())
            awaitComplete()
        }

        verify {
            callRepository.getCalls()
            instantToLocalDateUseCase(date1)
            instantToLocalDateUseCase(date2)
            instantToLocalDateUseCase(date3)

            formatLocalDateUseCase(groupDateFormat, localDate1)
            formatLocalDateUseCase(groupDateFormat, localDate2)

            formatInstantUseCase(chatDateFormat, date1)
            formatInstantUseCase(chatDateFormat, date2)
            formatInstantUseCase(chatDateFormat, date3)
        }
        confirmVerified(
            callRepository,
            instantToLocalDateUseCase,
            formatLocalDateUseCase,
            formatInstantUseCase
        )
    }
}
