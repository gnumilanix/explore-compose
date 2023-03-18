package com.ignitetech.compose.domain

import junit.framework.TestCase
import kotlinx.datetime.LocalDateTime
import org.junit.Test

class FormatLocalDateUseCaseTest {

    @Test
    fun `invoke formats Instant to given format`() {
        val format = "YYYY-MM-dd"
        val expectedDate = "2023-03-03"
        val expectedDateTime = "${expectedDate}T01:01"
        val date = LocalDateTime.parse(expectedDateTime).date
        val useCase = FormatLocalDateUseCase()

        TestCase.assertEquals(expectedDate, useCase(format, date))
    }
}