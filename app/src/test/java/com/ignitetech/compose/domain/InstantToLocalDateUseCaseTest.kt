package com.ignitetech.compose.domain

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.*
import org.junit.Rule
import org.junit.Test

class InstantToLocalDateUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var timeZone: TimeZone

    @MockK
    private lateinit var localDate: LocalDate

    @MockK
    private lateinit var localDateTime: LocalDateTime

    @MockK
    private lateinit var instant: Instant

    @InjectMockKs
    private lateinit var instantToLocalDateUseCase: InstantToLocalDateUseCase

    @Test
    fun invoke_ConvertsInstant_ToLocalDate() {
        mockkStatic("kotlinx.datetime.TimeZoneKt")
        every { localDateTime.date } returns localDate
        every { instant.toLocalDateTime(timeZone) } returns localDateTime

        assertEquals(instantToLocalDateUseCase(instant), localDate)
    }
}