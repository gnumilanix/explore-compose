package com.ignitetech.compose.data.preference

import androidx.datastore.preferences.core.*
import com.ignitetech.compose.rules.TestDispatcherRule
import com.ignitetech.compose.utility.Constants
import io.mockk.*
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { tmpFolder.newFile("${Constants.PREFERENCES_SETTINGS}.preferences_pb") }
    )

    @Test
    fun `onboardCompleteFlow sets and emits values correctly`() = testScope.runTest {
        val expected = true

        val preferenceRepository = PreferenceRepository(dataStore)

        preferenceRepository.userId(1)
        assertFalse(preferenceRepository.onboardCompleteFlow.first())

        preferenceRepository.onboardComplete(expected)
        assertTrue(preferenceRepository.onboardCompleteFlow.first())
    }

    @Test
    fun `userId sets and emits values correctly`() = testScope.runTest {
        val expected = 1

        val preferenceRepository = PreferenceRepository(dataStore)

        preferenceRepository.onboardComplete(false)
        assertNull(preferenceRepository.userIdFlow.first())

        preferenceRepository.userId(expected)
        assertEquals(expected, preferenceRepository.userIdFlow.first())

        preferenceRepository.userId(null)
        assertNull(preferenceRepository.userIdFlow.first())
    }
}
