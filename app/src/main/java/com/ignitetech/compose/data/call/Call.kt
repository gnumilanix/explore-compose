package com.ignitetech.compose.data.call

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ignitetech.compose.data.user.User
import java.util.*

@Entity
data class Call(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "type") val type: Type,
    @ColumnInfo(name = "date") val date: Calendar = Calendar.getInstance(),
    @Ignore val caller: User? = null,
) {
    constructor(
        id: Int,
        userId: Int,
        duration: Int,
        type: Type,
        date: Calendar
    ) : this(id, userId, duration, type, date, null)

    companion object {
        const val FIELD_USER_ID = "user_id"
    }
}