package com.ignitetech.compose.data.group

import com.ignitetech.compose.data.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
) {
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

    fun getGroups(): Flow<List<Group>> {
        return flow {
            emit(
                listOf(
                    Group("Friends", moreUsers),
                    Group("Family", users),
                    Group("Friends", moreUsers),
                    Group("Family", users),
                    Group("Friends", moreUsers),
                    Group("Family", lotsOfUsers),
                )
            )
        }
    }
}