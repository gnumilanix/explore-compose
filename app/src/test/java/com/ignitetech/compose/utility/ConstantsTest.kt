package com.ignitetech.compose.utility

import org.junit.Assert.assertEquals
import org.junit.Test

class ConstantsTest {
    @Test
    fun verifyConstants() {
        assertEquals("compose-db", Constants.DATABASE_NAME)
        assertEquals("compose-db.json", Constants.DATABASE_SEED_FILE)
        assertEquals("settings_preferences", Constants.PREFERENCES_SETTINGS)
    }
}