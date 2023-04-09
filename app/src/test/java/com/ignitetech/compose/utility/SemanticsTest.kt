package com.ignitetech.compose.utility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import org.junit.Assert.assertThrows
import org.junit.Test

class SemanticsTest {
    @Test
    fun `get drawableId throws UnsupportedOperationException`() {
        assertThrows(UnsupportedOperationException::class.java) {
            Modifier.semantics {
                drawableId
            }
        }
    }

    @Test
    fun `get drawableUrl throws UnsupportedOperationException`() {
        assertThrows(UnsupportedOperationException::class.java) {
            Modifier.semantics {
                drawableUrl
            }
        }
    }

    @Test
    fun `get drawableVector throws UnsupportedOperationException`() {
        assertThrows(UnsupportedOperationException::class.java) {
            Modifier.semantics {
                drawableVector
            }
        }
    }

    @Test
    fun `get screen throws UnsupportedOperationException`() {
        assertThrows(UnsupportedOperationException::class.java) {
            Modifier.semantics {
                screen
            }
        }
    }
}
