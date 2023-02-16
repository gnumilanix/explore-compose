package com.example.compose

data class Conversation(val sender: User, val message: String, val direction: Direction)

data class User(val name: String, val avatar: String)

enum class Direction {
    SENT, RECEIVED
}