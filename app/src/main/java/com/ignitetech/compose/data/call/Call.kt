package com.ignitetech.compose.data.call

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
data class Call(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "type") val type: Type,
    @ColumnInfo(name = "date") val date: Instant = Clock.System.now(),
    @Ignore @Transient
    val caller: User? = null
) {
    constructor(
        id: Int?,
        userId: Int,
        duration: Int,
        type: Type,
        date: Instant
    ) : this(id, userId, duration, type, date, null)

    companion object {
        const val FIELD_USER_ID = "user_id"
    }
}
