package com.example.compose

object SampleData {
    val messages = mapOf(
        "yesterday" to listOf(
            Conversation(
                "John",
                "Hello Jack! How are you today? Can you me those presentations",
                "http://placekitten.com/200/300"
            ),
            Conversation(
                "Jack",
                "Hello John! I am good. How about you?",
                "http://placekitten.com/200/400"
            ),
            Conversation(
                "John",
                "I am good as well",
                "http://placekitten.com/200/300"
            )
        ),
        "moments ago" to listOf(
            Conversation(
                "Jack",
                "What are you doing these days?",
                "http://placekitten.com/200/400"
            )
        )
    )
}