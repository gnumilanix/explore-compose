package com.ignitetech.compose.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class Converters {
    @TypeConverter
    fun instantToTimestamp(instant: Instant): Long = instant.toEpochMilliseconds()

    @TypeConverter
    fun timestampToInstant(instant: Long): Instant = Instant.fromEpochMilliseconds(instant)
}