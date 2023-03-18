package com.ignitetech.compose.data.user

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.ignitetech.compose.data.AppDatabase
import com.ignitetech.compose.utility.rules.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDaoTest {
    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var userDao: UserDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = appDatabase.userDao()
    }

    @Test
    @Throws(IOException::class)
    fun getUserReturnsUser() = runTest {
        val user = User(1, "John", "http://www.example.com/1")

        userDao.saveUser(user.id, user.name, user.avatar)

        assertEquals(user, userDao.getUser(1))
    }

    @Test
    @Throws(IOException::class)
    fun getUserFlowReturnsUser() = runTest {
        val user = User(1, "John", "http://www.example.com/1")

        userDao.saveUser(user.id, user.name, user.avatar)
        userDao.getUserFlow(1).test {
            assertEquals(user, awaitItem())
            cancel()
        }
    }

    @Test
    @Throws(IOException::class)
    fun getUserWithIdsReturnsUsers() = runTest {
        val user1 = User(1, "John", "http://www.example.com/1")
        val user2 = User(2, "Jack", "http://www.example.com/2")
        val user3 = User(3, "James", "http://www.example.com/3")

        userDao.saveUsers(user1, user2, user3)
        userDao.getUsers(1, 2).test {
            awaitItem().also { calls ->
                assert(calls.size == 2)
                assert(calls.contains(user1))
                assert(calls.contains(user2))
                cancel()
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun getAllReturnsAll() = runTest {
        val user1 = User(1, "John", "http://www.example.com/1")
        val user2 = User(2, "Jack", "http://www.example.com/2")

        userDao.saveUsers(user1, user2)
        userDao.getAll().test {
            awaitItem().also { calls ->
                assert(calls.size == 2)
                assert(calls.contains(user1))
                assert(calls.contains(user2))
                cancel()
            }
        }
    }
}