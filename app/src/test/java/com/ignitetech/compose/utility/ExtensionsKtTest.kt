package com.ignitetech.compose.utility

import androidx.compose.ui.focus.FocusState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun `FocusState isActive returns correct result`() {
        assertFalse(
            TestFocusState(hasFocus = false, isCaptured = false, isFocused = false).isActive
        )
        assertTrue(
            TestFocusState(hasFocus = false, isCaptured = true, isFocused = true).isActive
        )
        assertTrue(
            TestFocusState(hasFocus = true, isCaptured = false, isFocused = true).isActive
        )
        assertTrue(
            TestFocusState(hasFocus = true, isCaptured = true, isFocused = false).isActive
        )
        assertTrue(
            TestFocusState(hasFocus = true, isCaptured = true, isFocused = true).isActive
        )
    }

    class TestFocusState(
        override val hasFocus: Boolean,
        override val isCaptured: Boolean,
        override val isFocused: Boolean
    ) : FocusState
}
