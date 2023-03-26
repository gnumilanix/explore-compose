package com.ignitetech.compose.ui.call

import app.cash.turbine.test
import com.ignitetech.compose.domain.CallsByDate
import com.ignitetech.compose.domain.GetCallByDateUseCase
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.*
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
class CallViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var getCallByDateUseCase: GetCallByDateUseCase

    lateinit var viewModel: CallViewModel

    @Test
    fun `state returns default CallUiState initially`() = runTest {
        every { getCallByDateUseCase() } returns flowOf()

        viewModel = CallViewModel(getCallByDateUseCase)

        val state = viewModel.state.value

        assertEquals(CallUiState(), state)
        verify { getCallByDateUseCase() }
    }

    @Test
    fun `state returns updated CallUiState when calls updates`() = runTest {
        val callsByDates = listOf<CallsByDate>(mockk(), mockk())

        every { getCallByDateUseCase() } returns flow {
            delay(100)
            emit(callsByDates)
        }

        viewModel = CallViewModel(getCallByDateUseCase)

        viewModel.state.test {
            assertEquals(CallUiState(), awaitItem())

            advanceTimeBy(200)
            assertEquals(CallUiState(callsByDates), awaitItem())
        }
    }
}
