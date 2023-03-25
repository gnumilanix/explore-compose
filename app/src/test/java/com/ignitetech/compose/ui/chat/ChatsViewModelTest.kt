package com.ignitetech.compose.ui.chat

import app.cash.turbine.test
import com.ignitetech.compose.data.chat.Chat
import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.chat.ChatWithSender
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.domain.FormatInstantUseCase
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatsViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var chatRepository: ChatRepository

    @MockK
    lateinit var formatInstantUseCase: FormatInstantUseCase

    lateinit var viewModel: ChatsViewModel

    @Test
    fun `state returns default ChatDetail initially`() = runTest {
        coEvery { chatRepository.getLatestChats() } returns flowOf()
        viewModel = ChatsViewModel(chatRepository, formatInstantUseCase)

        val state = viewModel.state.value

        assertEquals(ChatsUiState(), state)
        coVerify { chatRepository.getLatestChats() }
    }

    @Test
    fun `state returns updated ChatDetail when chats updates`() = runTest {
        val dateString1 = "01/01"
        val date1 = mockk<Instant>()
        val chat1 = Chat(1, 1, "Hello", Direction.SENT, date1)
        val sender1 = User(1, "John", "http://www.example.com/1.jpg")
        val chatWithSender1 = ChatWithSender(chat1, sender1)

        val dateString2 = "01/01"
        val date2 = mockk<Instant>()
        val chat2 = Chat(2, 2, "Hi", Direction.RECEIVED, date2)
        val sender2 = User(2, "Jack", "http://www.example.com/2.jpg")
        val chatWithSender2 = ChatWithSender(chat2, sender2)

        val chats = listOf(chatWithSender1, chatWithSender2)

        val expectedState = ChatsUiState(
            listOf(
                ChatsUiState.ChatDetail(
                    chat1.id,
                    chat1.userId,
                    chat1.message,
                    chat1.direction,
                    dateString1,
                    chatWithSender1.sender
                ),
                ChatsUiState.ChatDetail(
                    chat2.id,
                    chat2.userId,
                    chat2.message,
                    chat2.direction,
                    dateString2,
                    chatWithSender2.sender
                )
            )
        )

        every { formatInstantUseCase("MM/dd", date1) } returns dateString1
        every { formatInstantUseCase("MM/dd", date2) } returns dateString2
        coEvery { chatRepository.getLatestChats() } returns flow {
            delay(100)
            emit(chats)
        }
        viewModel = ChatsViewModel(chatRepository, formatInstantUseCase)

        viewModel.state.test {
            assertEquals(ChatsUiState(), awaitItem())

            advanceTimeBy(200)
            assertEquals(expectedState, awaitItem())
        }
    }
}