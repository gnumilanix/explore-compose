package com.ignitetech.compose.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class FormatLocalDateUseCase @Inject constructor() {
    private val formatters = mutableMapOf<String, DateTimeFormatter>()

    operator fun invoke(format: String, date: LocalDate): String {
        val formatter = formatters[format] ?: DateTimeFormatter.ofPattern(format).also {
            formatters[format] = it
        }
        return formatter.format(date.toJavaLocalDate())
    }
}
