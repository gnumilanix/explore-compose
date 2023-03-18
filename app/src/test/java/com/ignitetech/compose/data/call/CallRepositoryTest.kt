package com.ignitetech.compose.data.call

import app.cash.turbine.test
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
class CallRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var callDao: CallDao

    @InjectMockKs
    lateinit var callRepository: CallRepository

    @Test
    fun `getCalls calls callDao getCallsWithTarget`() = runTest {
        val calls = listOf<CallWithTarget>(mockk(), mockk())

        every { callDao.getCallsWithTarget() } returns flowOf(calls)

        callRepository.getCalls().test {
            assertEquals(calls, awaitItem())
            awaitComplete()
        }

        verify { callDao.getCallsWithTarget() }
        confirmVerified(callDao)
    }

    @Test
    fun `saveCall calls callDao saveCalls with calls`() = runTest {
        val calls = listOf<Call>(mockk(), mockk()).toTypedArray()

        coJustRun { callDao.saveCalls(*anyVararg()) }

        callRepository.saveCall(*calls)

        coVerify { callDao.saveCalls(*calls) }
        confirmVerified(callDao)
    }

    @Test
    fun `deleteCall calls callDao deleteCalls with calls`() = runTest {
        val calls = listOf<Call>(mockk(), mockk()).toTypedArray()

        coJustRun { callDao.deleteCalls(*anyVararg()) }

        callRepository.deleteCall(*calls)

        coVerify { callDao.deleteCalls(*calls) }
        confirmVerified(callDao)
    }
}
