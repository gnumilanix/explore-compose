package com.ignitetech.compose.domain

import junit.framework.TestCase.assertEquals
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test

class InstantToLocalDateUseCaseTest {
    @Test
    fun `invoke converts Instant to LocalDate`() {
        val timeZone = TimeZone.of("Asia/Hong_Kong")
        val expectedDate = "2023-03-03T01:01"
        val date = LocalDateTime.parse(expectedDate)
        val instant = date.toInstant(timeZone)
        val useCase = InstantToLocalDateUseCase(timeZone)

        assertEquals(date.date, useCase(instant))
    }
}
