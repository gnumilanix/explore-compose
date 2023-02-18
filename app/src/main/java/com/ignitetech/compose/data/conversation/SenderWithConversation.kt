package com.ignitetech.compose.data.conversation

import androidx.room.Embedded
import androidx.room.Relation
import com.ignitetech.compose.data.user.User

data class SenderWithConversation(
    @Embedded
    val sender: User,

    @Relation(
        parentColumn = User.FIELD_ID,
        entityColumn = Conversation.FIELD_USER_ID
    )
    val conversation: Conversation
)