package com.ignitetech.compose.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class InstantToLocalDateUseCaseTest {
    @Mock
    private lateinit var timeZone: TimeZone

    @Mock
    private lateinit var localDate: LocalDate

    @Mock
    private lateinit var localDateTime: LocalDateTime

    @Mock
    private lateinit var instant: Instant

    @Test
    fun invoke_ConvertsInstant_ToLocalDate() {
        timeZone = mock {

        }
        localDateTime = mock {
            on { date } doReturn localDate
        }

        instant = mockk {}
        mockkStatic("kotlinx.datetime.TimeZoneKt")
        every { instant.toLocalDateTime(timeZone) } returns localDateTime

        val instantToLocalDateUseCase = InstantToLocalDateUseCase(timeZone)

        assertEquals(instantToLocalDateUseCase(instant), localDate)
    }
}