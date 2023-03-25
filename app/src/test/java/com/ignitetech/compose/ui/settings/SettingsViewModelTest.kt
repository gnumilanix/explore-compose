package com.ignitetech.compose.ui.settings

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.Snapshot.Companion.withMutableSnapshot
import app.cash.turbine.test
import com.ignitetech.compose.R
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserRepository
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
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
class SettingsViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var selectedAvatar: Bitmap

    lateinit var viewModel: SettingsViewModel

    @Test
    fun `state returns default HomeUiState initially`() = runTest {
        coEvery { userRepository.getMe() } returns flowOf()
        viewModel = SettingsViewModel(userRepository)

        val state = viewModel.state.value

        assertEquals(SettingsUiState(null, R.string.lorem_ipsum), state)
        coVerify { userRepository.getMe() }
    }

    @Test
    fun `state returns updated SettingsUiState when user updates`() = runTest {
        val user = User(1, "John", "http://www.example.com/1.jpg")

        coEvery { userRepository.getMe() } returns flow {
            delay(100)
            emit(user)
        }
        viewModel = SettingsViewModel(userRepository)

        viewModel.state.test {
            assertEquals(SettingsUiState(null, R.string.lorem_ipsum), awaitItem())

            advanceTimeBy(200)
            assertEquals(SettingsUiState(user.avatar, R.string.lorem_ipsum), awaitItem())
        }
    }

    @Test
    fun `state returns updated SettingsUiState when selectedAvatar updates`() = runTest {
        val user = User(1, "John", "http://www.example.com/1.jpg")

        coEvery { userRepository.getMe() } returns flowOf(user)

        viewModel = SettingsViewModel(userRepository)

        viewModel.state.test {
            assertEquals(SettingsUiState(user.avatar, R.string.lorem_ipsum), awaitItem())

            withMutableSnapshot { viewModel.updateAvatar(selectedAvatar) }
            assertEquals(SettingsUiState(selectedAvatar, R.string.lorem_ipsum), awaitItem())
        }
    }
}