package com.ignitetech.compose.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    @Test
    fun `instantToTimestamp returns toEpochMilliseconds`() {
        val now: Instant = Clock.System.now()

        assertEquals(
            (now.toEpochMilliseconds() / 1000) * 1000,
            Converters().instantToTimestamp(now)
        )
    }

    @Test
    fun `timestampToInstant returns Instant`() {
        val now = Clock.System.now().toEpochMilliseconds()

        assertEquals(Instant.fromEpochMilliseconds(now), Converters().timestampToInstant(now))
    }
}
