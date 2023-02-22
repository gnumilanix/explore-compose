package com.ignitetech.compose.data.seed

import com.ignitetech.compose.data.call.Call
import com.ignitetech.compose.data.chat.Chat
import com.ignitetech.compose.data.user.User
import kotlinx.serialization.Serializable

@Serializable
data class Seed(
    val users: List<User>,
    val chats: List<Chat>,
    val calls: List<Call>
)