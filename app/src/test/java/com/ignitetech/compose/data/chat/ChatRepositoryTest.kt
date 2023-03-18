package com.ignitetech.compose.data.chat

import app.cash.turbine.test
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var chatDao: ChatDao

    @InjectMockKs
    lateinit var chatRepository: ChatRepository

    @Test
    fun `getLatestChats with senderId calls chatDao getLatestChatsWithSender`() = runTest {
        val chats = listOf<ChatWithSender>(mockk(), mockk())

        every { chatDao.getLatestChatsWithSender() } returns flowOf(chats)

        chatRepository.getLatestChats().test {
            assertEquals(chats, awaitItem())
            awaitComplete()
        }

        verify { chatDao.getLatestChatsWithSender() }
        confirmVerified(chatDao)
    }

    @Test
    fun `getChats with senderId calls chatDao getChatsWithSender`() = runTest {
        val senderId = 1
        val chats = listOf<ChatWithSender>(mockk(), mockk())

        every { chatDao.getChatsWithSender(any()) } returns flowOf(chats)

        chatRepository.getChats(senderId).test {
            assertEquals(chats, awaitItem())
            awaitComplete()
        }

        verify { chatDao.getChatsWithSender(senderId) }
        confirmVerified(chatDao)
    }
}
