package com.ignitetech.compose.data.chat

import androidx.room.Embedded
import androidx.room.Relation
import com.ignitetech.compose.data.user.User

data class ChatWithSender(
    @Embedded
    val chat: Chat,

    @Relation(
        parentColumn = Chat.FIELD_USER_ID,
        entityColumn = User.FIELD_ID
    )
    val sender: User
)
