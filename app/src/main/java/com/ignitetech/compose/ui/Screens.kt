package com.ignitetech.compose.ui

sealed class Screens(val route: String) {
    object Splash : Screens("Splash")
    object Home : Screens("Home")
    object Onboard : Screens("Onboard")

    object Chats : Screens("Chats/{userId}") {
        fun route(id: Int) = "Chats/$id"
    }
}
