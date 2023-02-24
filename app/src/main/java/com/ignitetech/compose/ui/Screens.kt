package com.ignitetech.compose.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ignitetech.compose.R

sealed class Screens(val route: String) {
    object Splash : Screens("Splash")
    object Home : Screens("Home")
    object Onboard : Screens("Onboard")
    object Settings : Screens("Settings")

    sealed class HomeScreens(
        _route: String,
        @StringRes val name: Int,
        @DrawableRes val icon: Int
    ) : Screens(_route) {
        object Chats : HomeScreens("Chats", R.string.chats, R.drawable.baseline_chat_24)
        object Groups : HomeScreens("Groups", R.string.groups, R.drawable.baseline_groups_24)
        object Calls : HomeScreens("Calls", R.string.calls, R.drawable.baseline_call_24)
    }

    object Chats : Screens("Chats/{userId}") {
        fun route(id: Int) = "Chats/$id"
    }
}
