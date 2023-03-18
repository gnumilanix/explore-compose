package com.ignitetech.compose.domain

import junit.framework.TestCase
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test

class FormatInstantUseCaseTest {

    @Test
    fun `invoke formats Instant to given format`() {
        val timeZone = TimeZone.of("Asia/Hong_Kong")
        val format = "YYYY-MM-dd'T'HH:mm"
        val expectedDate = "2023-03-03T01:01"
        val date = LocalDateTime.parse(expectedDate).toInstant(timeZone)
        val useCase = FormatInstantUseCase(timeZone)

        TestCase.assertEquals(expectedDate, useCase(format, date))
    }
}