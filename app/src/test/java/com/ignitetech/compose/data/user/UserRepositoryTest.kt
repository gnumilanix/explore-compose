package com.ignitetech.compose.data.user

import app.cash.turbine.test
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var userDao: UserDao

    @MockK
    lateinit var preferenceRepository: PreferenceRepository

    @InjectMockKs
    lateinit var userRepository: UserRepository

    @Test
    fun `getMe returns null if current user missing otherwise user`() = runTest {
        val id = 1
        val user = mockk<User>()

        every { preferenceRepository.userIdFlow } returns flowOf(null, id)
        coEvery { userDao.getUser(any()) } returns user

        userRepository.getMe().test {
            assertEquals(null, awaitItem())
            assertEquals(user, awaitItem())
            awaitComplete()
        }

        verify { preferenceRepository.userIdFlow }
        coVerify { userDao.getUser(id) }
        confirmVerified(preferenceRepository, userDao)
    }

    @Test
    fun `getUsers returns all users`() = runTest {
        val users = listOf<User>(mockk(), mockk())

        every { userDao.getAll() } returns flowOf(users)

        userRepository.getUsers().test {
            assertEquals(users, awaitItem())
            awaitComplete()
        }

        verify { userDao.getAll() }
        confirmVerified(userDao)
    }

    @Test
    fun `getUsers returns users matching id`() = runTest {
        val ids = intArrayOf(1, 2)
        val users = listOf<User>(mockk(), mockk())

        every { userDao.getUsers(*anyIntVararg()) } returns flowOf(users)

        userRepository.getUsers(*ids).test {
            assertEquals(users, awaitItem())
            awaitComplete()
        }

        verify { userDao.getUsers(*ids) }
        confirmVerified(userDao)
    }

    @Test
    fun `getUser returns single user`() = runTest {
        val id = 1
        val user = mockk<User>()

        every { userDao.getUserFlow(any()) } returns flowOf(user)

        userRepository.getUser(id).test {
            assertEquals(user, awaitItem())
            awaitComplete()
        }

        verify { userDao.getUserFlow(id) }
        confirmVerified(userDao)
    }
}