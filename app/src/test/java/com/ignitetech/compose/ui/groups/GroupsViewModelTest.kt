package com.ignitetech.compose.ui.groups

import app.cash.turbine.test
import com.ignitetech.compose.data.group.Group
import com.ignitetech.compose.data.group.GroupRepository
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
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
class GroupsViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var groupsRepository: GroupRepository

    lateinit var viewModel: GroupsViewModel

    @Test
    fun `state returns default GroupsUiState initially`() = runTest {
        coEvery { groupsRepository.getGroups() } returns flowOf()
        viewModel = GroupsViewModel(groupsRepository)

        val state = viewModel.state.value

        assertEquals(GroupsUiState(), state)
        coVerify { groupsRepository.getGroups() }
    }

    @Test
    fun `state returns updated GroupsUiState when groups updates`() = runTest {
        val groups = listOf<Group>(mockk(), mockk())
        coEvery { groupsRepository.getGroups() } returns flow {
            delay(100)
            emit(groups)
        }
        viewModel = GroupsViewModel(groupsRepository)

        viewModel.state.test {
            assertEquals(GroupsUiState(), awaitItem())

            advanceTimeBy(200)
            assertEquals(GroupsUiState(groups), awaitItem())
        }
    }
}