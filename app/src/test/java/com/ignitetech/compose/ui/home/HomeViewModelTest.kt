package com.ignitetech.compose.ui.home

import app.cash.turbine.test
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.rules.TestDispatcherRule
import com.ignitetech.compose.ui.Screens
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var preferenceRepository: PreferenceRepository

    lateinit var viewModel: HomeViewModel

    private val defaultTabs = listOf(
        Screens.HomeScreens.Chats,
        Screens.HomeScreens.Groups,
        Screens.HomeScreens.Calls
    )

    @Test
    fun `onboardComplete sets preferenceRepository_onboardComplete true`() = runTest {
        coEvery { preferenceRepository.onboardCompleteFlow } returns flowOf()
        coEvery { preferenceRepository.userId(any()) } returns Unit
        coEvery { preferenceRepository.onboardComplete(any()) } returns Unit
        viewModel = HomeViewModel(preferenceRepository)

        viewModel.onboardComplete()

        coVerify {
            preferenceRepository.userId(0)
            preferenceRepository.onboardComplete(true)
        }
    }

    @Test
    fun `state returns default HomeUiState initially`() = runTest {
        coEvery { preferenceRepository.onboardCompleteFlow } returns flowOf()
        viewModel = HomeViewModel(preferenceRepository)

        val state = viewModel.state.value

        assertNull(state.onboardComplete)
        assertEquals(defaultTabs, state.tabs)
        coVerify { preferenceRepository.onboardCompleteFlow }
    }

    @Test
    fun `state returns HomeUiState when onboardCompleteFlow updates`() = runTest {
        coEvery { preferenceRepository.onboardCompleteFlow } returns flow {
            delay(100)
            emit(true)
        }
        viewModel = HomeViewModel(preferenceRepository)

        viewModel.state.test {
            assertState(null, defaultTabs, awaitItem())
            advanceTimeBy(200)
            assertState(true, defaultTabs, awaitItem())
        }
    }

    private fun assertState(
        expectedOnboardComplete: Boolean?,
        expectedTabs: List<Screens.HomeScreens>,
        updatedState: HomeUiState
    ) {
        assertEquals(expectedOnboardComplete, updatedState.onboardComplete)
        assertEquals(expectedTabs, updatedState.tabs)
    }
}