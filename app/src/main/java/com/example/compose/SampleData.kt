package com.example.compose

object SampleData {
    val userJohn = User("John", "http://placekitten.com/200/300")
    val userJack = User("Jack", "http://placekitten.com/200/400")
    val messages = mapOf(
        "yesterday" to listOf(
            Conversation(
                userJohn,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT
            ),
            Conversation(
                userJack,
                "Hello John! I am good. How about you?",
                Direction.RECEIVED
            ),
            Conversation(
                userJohn,
                "I am good as well",
                Direction.SENT
            )
        ),
        "moments ago" to listOf(
            Conversation(
                userJack,
                "What are you doing these days?",
                Direction.RECEIVED
            )
        )
    )
}