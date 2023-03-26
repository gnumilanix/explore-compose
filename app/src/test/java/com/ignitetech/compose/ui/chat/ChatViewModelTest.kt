package com.ignitetech.compose.ui.chat

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import com.ignitetech.compose.domain.ChatsByDate
import com.ignitetech.compose.domain.GetChatByDateUseCase
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var getChatByDateUseCase: GetChatByDateUseCase

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    @MockK
    lateinit var me: User

    @MockK
    lateinit var recipient: User

    @MockK
    lateinit var chatsByDates: List<ChatsByDate>

    lateinit var viewModel: ChatViewModel

    @Test
    fun `state returns default ChatUiState initially`() = runTest {
        val recipientId = 100

        every { savedStateHandle.get<Int>(any()) } returns recipientId
        every { userRepository.getMe() } returns flowOf()
        every { userRepository.getUser(any()) } returns flowOf()
        every { getChatByDateUseCase(any()) } returns flowOf()

        viewModel = ChatViewModel(userRepository, getChatByDateUseCase, savedStateHandle)

        val state = viewModel.state.value

        assertEquals(ChatUiState(), state)
        verify {
            savedStateHandle.get<Int>(ChatViewModel.RecipientId)
            userRepository.getMe()
            userRepository.getUser(recipientId)
            getChatByDateUseCase(recipientId)
        }
    }

    @Test
    fun `state returns updated ChatUiState when me updates`() = runTest {
        val initialMe = mockk<User>()

        every { savedStateHandle.get<Int>(any()) } returns 100
        every { userRepository.getMe() } returns flow {
            emit(initialMe)

            delay(100)
            emit(me)
        }
        every { userRepository.getUser(any()) } returns flowOf(recipient)
        every { getChatByDateUseCase(any()) } returns flowOf(chatsByDates)

        viewModel = ChatViewModel(userRepository, getChatByDateUseCase, savedStateHandle)

        viewModel.state.test {
            assertEquals(ChatUiState(initialMe, recipient, chatsByDates), awaitItem())

            advanceTimeBy(200)
            assertEquals(ChatUiState(me, recipient, chatsByDates), awaitItem())
        }
    }

    @Test
    fun `state returns updated ChatUiState when recipient updates`() = runTest {
        val initialRecipient = mockk<User>()

        every { savedStateHandle.get<Int>(any()) } returns 100
        every { userRepository.getMe() } returns flowOf(me)
        every { userRepository.getUser(any()) } returns flow {
            emit(initialRecipient)

            delay(100)
            emit(recipient)
        }
        every { getChatByDateUseCase(any()) } returns flowOf(chatsByDates)

        viewModel = ChatViewModel(userRepository, getChatByDateUseCase, savedStateHandle)

        viewModel.state.test {
            assertEquals(ChatUiState(me, initialRecipient, chatsByDates), awaitItem())

            advanceTimeBy(200)
            assertEquals(ChatUiState(me, recipient, chatsByDates), awaitItem())
        }
    }

    @Test
    fun `state returns updated ChatUiState when chats updates`() = runTest {
        val initialChats = mockk<List<ChatsByDate>>()
        every { savedStateHandle.get<Int>(any()) } returns 100
        every { userRepository.getMe() } returns flowOf(me)
        every { userRepository.getUser(any()) } returns flowOf(recipient)
        every { getChatByDateUseCase(any()) } returns flow {
            emit(initialChats)

            delay(100)
            emit(chatsByDates)
        }

        viewModel = ChatViewModel(userRepository, getChatByDateUseCase, savedStateHandle)

        viewModel.state.test {
            assertEquals(ChatUiState(me, recipient, initialChats), awaitItem())

            advanceTimeBy(200)
            assertEquals(ChatUiState(me, recipient, chatsByDates), awaitItem())
        }
    }
}
