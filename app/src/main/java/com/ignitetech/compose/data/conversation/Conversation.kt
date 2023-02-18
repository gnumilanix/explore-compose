package com.ignitetech.compose.data.conversation

import androidx.room.*
import com.ignitetech.compose.data.user.User

@Entity
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "direction") val direction: Direction,
    @Ignore val sender: User? = null,
) {
    constructor(
        id: Int,
        userId: Int,
        message: String,
        direction: Direction
    ) : this(id, userId, message, direction, null)

    companion object {
        const val FIELD_USER_ID = "user_id"
    }
}