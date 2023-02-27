package com.ignitetech.compose.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.group.Group
import com.ignitetech.compose.data.group.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    groupsRepository: GroupRepository
) : ViewModel() {
    private val groups = groupsRepository.getGroups()

    val state = groups
        .map { GroupsUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = GroupsUiState()
        )
}

data class GroupsUiState(
    val groups: List<Group> = listOf()
)