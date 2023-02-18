package com.ignitetech.compose.data.conversation

import com.ignitetech.compose.data.user.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepository @Inject constructor() {
    fun getConversations(): List<Conversation> {
        return listOf(
            Conversation(
                1,
                1,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT,
                User(1, "John", "http://placekitten.com/200/300")
            ),
            Conversation(
                2,
                2,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT,
                User(2, "Jane", "http://placekitten.com/200/100")
            )
        )
    }

    fun getConversations(id: Int): Map<String, List<Conversation>> {
        return mapOf(
            "yesterday" to listOf(
                Conversation(
                    1,
                    1,
                    "Hello Jack! How are you today? Can you me those presentations",
                    Direction.SENT,
                    User(1, "John", "http://placekitten.com/200/300")
                ),
                Conversation(
                    2,
                    2,
                    "Hello John! I am good. How about you?",
                    Direction.RECEIVED,
                    User(2, "Jane", "http://placekitten.com/200/100")
                ),
                Conversation(
                    3,
                    1,
                    "I am good as well",
                    Direction.SENT,
                    User(1, "John", "http://placekitten.com/200/300")
                )
            ),
            "moments ago" to listOf(
                Conversation(
                    4,
                    2,
                    "What are you doing these days?",
                    Direction.RECEIVED,
                    User(2, "Jane", "http://placekitten.com/200/100")
                )
            )
        )
    }
}