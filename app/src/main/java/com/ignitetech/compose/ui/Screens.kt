package com.ignitetech.compose.ui

sealed class Screens(val route: String) {
    object Home : Screens("Home")

    object Chats : Screens("Chats/{userId}") {
        fun route(id: Int) = "Chats/$id"
    }
}
