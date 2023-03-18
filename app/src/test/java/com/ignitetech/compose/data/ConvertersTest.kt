package com.ignitetech.compose.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    @Test
    fun `instantToTimestamp returns toEpochMilliseconds`() {
        val now = Clock.System.now()

        assertEquals(now.toEpochMilliseconds(), Converters().instantToTimestamp(now))
    }

    @Test
    fun `timestampToInstant returns Instant`() {
        val now = Clock.System.now().toEpochMilliseconds()

        assertEquals(Instant.fromEpochMilliseconds(now), Converters().timestampToInstant(now))
    }
}
