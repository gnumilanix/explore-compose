package com.ignitetech.compose.data.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ignitetech.compose.data.user.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity
@Serializable
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "direction") val direction: Direction,
    @ColumnInfo(name = "date") val date: Instant = Clock.System.now(),
    @Ignore @Transient
    val sender: User? = null
) {
    constructor(
        id: Int,
        userId: Int,
        message: String,
        direction: Direction,
        date: Instant
    ) : this(id, userId, message, direction, date, null)

    companion object {
        const val FIELD_USER_ID = "user_id"
    }
}
