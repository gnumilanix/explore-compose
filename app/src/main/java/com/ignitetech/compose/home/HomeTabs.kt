package com.ignitetech.compose.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ignitetech.compose.R

sealed class HomeTabs(@StringRes val name: Int, @DrawableRes val icon: Int) {
    object Chat : HomeTabs(R.string.chats, R.drawable.baseline_chat_24)
    object Group : HomeTabs(R.string.groups, R.drawable.baseline_groups_24)
    object Call : HomeTabs(R.string.calls, R.drawable.baseline_call_24)
}