package com.ignitetech.compose.utility

import androidx.compose.ui.focus.FocusState

val FocusState.isActive: Boolean
    get() {
        return hasFocus || isFocused || isCaptured
    }