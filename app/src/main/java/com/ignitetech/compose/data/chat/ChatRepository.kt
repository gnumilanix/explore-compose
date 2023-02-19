package com.ignitetech.compose.data.chat

import com.ignitetech.compose.data.user.User
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor() {
    fun getChats(): List<Chat> {
        return listOf(
            Chat(
                1,
                1,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT,
                Calendar.getInstance(),
                User(1, "John", "http://placekitten.com/200/300")
            ),
            Chat(
                2,
                2,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT,
                Calendar.getInstance(),
                User(2, "Jane", "http://placekitten.com/200/100")
            )
        )
    }

    fun getChats(id: Int): Map<String, List<Chat>> {
        return mapOf(
            "yesterday" to listOf(
                Chat(
                    1,
                    1,
                    "Hello Jack! How are you today? Can you me those presentations",
                    Direction.SENT,
                    Calendar.getInstance(),
                    User(1, "John", "http://placekitten.com/200/300")
                ),
                Chat(
                    2,
                    2,
                    "Hello John! I am good. How about you?",
                    Direction.RECEIVED,
                    Calendar.getInstance(),
                    User(2, "Jane", "http://placekitten.com/200/100")
                ),
                Chat(
                    3,
                    1,
                    "I am good as well",
                    Direction.SENT,
                    Calendar.getInstance(),
                    User(1, "John", "http://placekitten.com/200/300")
                )
            ),
            "moments ago" to listOf(
                Chat(
                    4,
                    2,
                    "What are you doing these days?",
                    Direction.RECEIVED,
                    Calendar.getInstance(),
                    User(2, "Jane", "http://placekitten.com/200/100")
                )
            )
        )
    }
}