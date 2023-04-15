package com.ignitetech.compose

import android.util.Log
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ComposeConfigurationTest {

    @Test
    fun `logLevel returns correct log level`() {
        assertEquals(Log.DEBUG, ComposeConfiguration.logLevel)
    }
}
