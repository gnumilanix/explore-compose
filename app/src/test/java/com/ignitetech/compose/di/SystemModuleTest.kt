package com.ignitetech.compose.di

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkObject
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SystemModuleTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var timeZone: TimeZone

    private val systemModule = SystemModule()

    @Test
    fun provideTimezone() {
        mockkObject(TimeZone)
        every { TimeZone.currentSystemDefault() } returns timeZone

        assertEquals(timeZone, systemModule.provideTimezone())
    }
}