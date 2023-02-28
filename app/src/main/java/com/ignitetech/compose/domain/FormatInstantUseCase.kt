package com.ignitetech.compose.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FormatInstantUseCase @Inject constructor(
    private val timeZone: TimeZone
) {
    private val formatters = mutableMapOf<String, DateTimeFormatter>()

    operator fun invoke(format: String, date: Instant): String {
        val formatter = formatters[format] ?: DateTimeFormatter.ofPattern(format).also {
            formatters[format] = it
        }
        return formatter.format(date.toLocalDateTime(timeZone).toJavaLocalDateTime())
    }
}