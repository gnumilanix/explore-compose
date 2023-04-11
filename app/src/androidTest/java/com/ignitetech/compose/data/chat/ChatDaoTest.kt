package com.ignitetech.compose.data.chat

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.ignitetech.compose.data.AppDatabase
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.utility.extensions.currentTimeInstant
import com.ignitetech.compose.utility.rules.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatDaoTest {
    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var chatDao: ChatDao
    private lateinit var userDao: UserDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        chatDao = appDatabase.chatDao()
        userDao = appDatabase.userDao()
    }

    @Test
    @Throws(IOException::class)
    fun getAllReturnsAll() = runTest {
        val now = currentTimeInstant()
        val chat1 = Chat(1, 1, "Hello", Direction.RECEIVED, now)
        val chat2 = Chat(2, 1, "Hi", Direction.SENT, now)

        chatDao.saveChats(chat1, chat2)
        chatDao.getAll().test {
            awaitItem().also { calls ->
                assert(calls.size == 2)
                assert(calls.contains(chat1))
                assert(calls.contains(chat2))
                cancel()
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getChatsWithSenderGetsChatsWithUser() = runTest {
        val now = currentTimeInstant()
        val chat1 = Chat(1, 1, "Hello", Direction.RECEIVED, now)
        val chat2 = Chat(2, 1, "Hi", Direction.SENT, now)
        val user1 = User(1, "John", "http://www.example.com/image1.jpeg")

        chatDao.saveChats(chat1, chat2)
        userDao.saveUsers(user1)
        chatDao.getChatsWithSender(1).test {
            awaitItem().also { chatWithSenders ->
                assert(chatWithSenders.size == 2)
                assert(chatWithSenders.contains(ChatWithSender(chat1, user1)))
                assert(chatWithSenders.contains(ChatWithSender(chat2, user1)))
                cancel()
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getLatestChatsWithSenderGetsLatestChatsWithUser() = runTest {
        val now = currentTimeInstant()
        val past = currentTimeInstant().minus(2.0.minutes)
        val user1Chat1 = Chat(1, 1, "Hello", Direction.RECEIVED, past)
        val user1Chat2 = Chat(2, 1, "Hi", Direction.SENT, now)
        val user2Chat1 = Chat(3, 2, "How are you?", Direction.RECEIVED, past)
        val user2Chat2 = Chat(4, 2, "I am fine", Direction.SENT, now)
        val user1 = User(1, "John", "http://www.example.com/image1.jpeg")
        val user2 = User(2, "Jack", "http://www.example.com/image2.jpeg")

        chatDao.saveChats(user1Chat1, user1Chat2, user2Chat1, user2Chat2)
        userDao.saveUsers(user1, user2)
        chatDao.getLatestChatsWithSender().test {
            awaitItem().also { chatWithSenders ->
                assert(chatWithSenders.size == 2)
                assert(chatWithSenders.contains(ChatWithSender(user1Chat2, user1)))
                assert(chatWithSenders.contains(ChatWithSender(user2Chat2, user2)))
                cancel()
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteChatsDeletesGivenChats() = runTest {
        val now = currentTimeInstant()
        val chat1 = Chat(1, 1, "Hello", Direction.RECEIVED, now)
        val chat2 = Chat(2, 1, "Hi", Direction.SENT, now)

        chatDao.saveChats(chat1, chat2)
        chatDao.deleteChats(chat1)
        chatDao.getAll().test {
            awaitItem().also { calls ->
                assert(calls.size == 1)
                assert(calls.contains(chat2))
                cancel()
            }
        }
    }
}
