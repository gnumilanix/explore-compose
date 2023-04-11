package com.ignitetech.compose.utility.extensions

import com.ignitetech.compose.data.Converters
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun currentTimeInstant(): Instant {
    val converters = Converters()

    return converters.timestampToInstant(converters.instantToTimestamp(Clock.System.now()))
}
