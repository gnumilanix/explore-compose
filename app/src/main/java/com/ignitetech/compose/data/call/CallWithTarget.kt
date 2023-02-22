package com.ignitetech.compose.data.call

import androidx.room.Embedded
import androidx.room.Relation
import com.ignitetech.compose.data.user.User

data class CallWithTarget(
    @Embedded
    val call: Call,

    @Relation(
        parentColumn = Call.FIELD_USER_ID,
        entityColumn = User.FIELD_ID
    )
    val target: User
)