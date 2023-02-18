package com.ignitetech.compose.data.user

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    fun getMe(): User {
        return User(0, "Jack", "http://placekitten.com/200/400")
    }

    fun getUsers(): List<User> {
        return listOf(
            User(1, "John", "http://placekitten.com/200/300"),
            User(2, "Jane", "http://placekitten.com/200/100")
        )
    }

    fun getUser(id: Int): User? {
        return listOf(
            User(0, "Jack", "http://placekitten.com/200/400"),
            User(1, "John", "http://placekitten.com/200/300"),
            User(2, "Jane", "http://placekitten.com/200/100")
        ).firstOrNull { it.id == id }
    }
}