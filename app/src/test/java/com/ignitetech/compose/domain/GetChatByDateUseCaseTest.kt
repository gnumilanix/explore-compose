package com.ignitetech.compose.domain

import app.cash.turbine.test
import com.ignitetech.compose.data.chat.Chat
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.chat.ChatWithSender
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetChatByDateUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var chatRepository: ChatRepository

    @MockK
    lateinit var formatInstantUseCase: FormatInstantUseCase

    @MockK
    lateinit var formatLocalDateUseCase: FormatLocalDateUseCase

    @MockK
    lateinit var instantToLocalDateUseCase: InstantToLocalDateUseCase

    @InjectMockKs
    lateinit var getChatByDateUseCase: GetChatByDateUseCase

    @Test
    fun `invoke maps calls by date correctly`() = runTest {
        val recipientId = 100
        val groupDateFormat = "MMMM dd"
        val chatDateFormat = "MM/dd"

        val user1 = User(1, "John", "https://placekitten.com/200/300")
        val user2 = User(2, "Jane", "https://placekitten.com/200/100")

        val localDate1 = mockk<LocalDate>()
        val localDate2 = mockk<LocalDate>()
        val localDateString1 = "January 01"
        val localDateString2 = "January 02"

        val dateString1 = "01/01"
        val date1 = mockk<Instant>()
        val chat1 = Chat(0, 1, "Hello", Direction.RECEIVED, date1)
        val chatWithSender1 = ChatWithSender(chat1, user1)

        val dateString2 = "01/01"
        val date2 = mockk<Instant>()
        val chat2 = Chat(1, 2, "Hi", Direction.SENT, date2)
        val chatWithSender2 = ChatWithSender(chat2, user2)

        val date3 = mockk<Instant>()
        val dateString3 = "01/02"
        val chat3 = Chat(3, 1, "Umm", Direction.SENT, date3)
        val chatWithSender3 = ChatWithSender(chat3, user1)

        val chatByDates = listOf(chatWithSender1, chatWithSender2, chatWithSender3)
        val expectedFlow = listOf(
            ChatsByDate(
                localDateString1,
                listOf(
                    ChatDetail(
                        chat1.id,
                        chat1.userId,
                        chat1.message,
                        chat1.direction,
                        dateString1,
                        user1
                    ),
                    ChatDetail(
                        chat2.id,
                        chat2.userId,
                        chat2.message,
                        chat2.direction,
                        dateString2,
                        user2
                    )
                )
            ),
            ChatsByDate(
                localDateString2,
                listOf(
                    ChatDetail(
                        chat3.id,
                        chat3.userId,
                        chat3.message,
                        chat3.direction,
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

        every { chatRepository.getChats(recipientId) } returns flow {
            delay(100)
            emit(chatByDates)
        }

        getChatByDateUseCase(recipientId).test {
            assertEquals(expectedFlow, awaitItem())
            awaitComplete()
        }

        verify {
            chatRepository.getChats(recipientId)
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
            chatRepository,
            instantToLocalDateUseCase,
            formatLocalDateUseCase,
            formatInstantUseCase
        )
    }
}
