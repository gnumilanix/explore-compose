package com.ignitetech.compose.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class InstantToLocalDateUseCase @Inject constructor(
    private val timeZone: TimeZone
) {
    operator fun invoke(instant: Instant): LocalDate {
        return instant.toLocalDateTime(timeZone).date
    }
}
