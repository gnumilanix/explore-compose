package com.ignitetech.compose.data.call

import androidx.room.Embedded
import androidx.room.Relation
import com.ignitetech.compose.data.user.User

data class CallerWithCall(
    @Embedded
    val caller: User,

    @Relation(
        parentColumn = User.FIELD_ID,
        entityColumn = Call.FIELD_USER_ID
    )
    val call: Call
)