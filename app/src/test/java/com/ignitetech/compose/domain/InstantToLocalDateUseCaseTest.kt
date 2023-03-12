package com.ignitetech.compose.domain

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
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

    @Test
    fun invoke_ConvertsInstant_ToLocalDate() {
        timeZone = mockk {

        }
        localDateTime = mockk {
            every { date } returns localDate
        }

        instant = mockk {}
        mockkStatic("kotlinx.datetime.TimeZoneKt")
        every { instant.toLocalDateTime(timeZone) } returns localDateTime

        val instantToLocalDateUseCase = InstantToLocalDateUseCase(timeZone)

        assertEquals(instantToLocalDateUseCase(instant), localDate)
    }
}