package com.ignitetech.compose.ui

object Routes {
    const val Home = "Home"
    const val Chats = "${Destination.Chats}/{userId}"
}

object Destination {
    const val Chats = "Chats"
}

object Arguments {
    const val UserId = "userId"
}