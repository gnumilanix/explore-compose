package com.ignitetech.compose.data.chat

import androidx.room.Embedded
import androidx.room.Relation
import com.ignitetech.compose.data.user.User

data class SenderWithChat(
    @Embedded
    val sender: User,

    @Relation(
        parentColumn = User.FIELD_ID,
        entityColumn = Chat.FIELD_USER_ID
    )
    val chat: Chat
)