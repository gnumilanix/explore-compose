package com.ignitetech.compose.ui.groups

import androidx.lifecycle.ViewModel
import com.ignitetech.compose.data.group.Group
import com.ignitetech.compose.data.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor() : ViewModel() {
    private val _groups = MutableStateFlow(listOf<Group>())
    val groups = _groups.asStateFlow()

    init {
        _groups.update { sampleGroups }
    }
}

private val users = listOf(
    User(1, "John", "https://placekitten.com/200/300"),
    User(1, "Jack", "https://placekitten.com/300/200")
)
private val moreUsers = listOf(
    User(1, "John", "https://placekitten.com/200/300"),
    User(1, "Jack", "https://placekitten.com/300/200"),
    User(1, "Jane", "https://placekitten.com/200/400")
)
private val lotsOfUsers = listOf(
    User(1, "John", "https://placekitten.com/200/300"),
    User(1, "Jack", "https://placekitten.com/300/200"),
    User(1, "Jane", "https://placekitten.com/200/400"),
    User(1, "Amy", "https://placekitten.com/300/300"),
    User(1, "Cindy", "https://placekitten.com/200/200"),
    User(1, "Mandy", "https://placekitten.com/400/300")
)

val sampleGroups = listOf(
    Group("Friends", moreUsers),
    Group("Family", users),
    Group("Friends", moreUsers),
    Group("Family", users),
    Group("Friends", moreUsers),
    Group("Family", lotsOfUsers),
)