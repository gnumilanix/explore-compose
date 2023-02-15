package com.example.compose

data class Conversation(val sender: User, val message: String)

data class User(val name: String, val avatar: String)