package com.ignitetech.compose.utility.actions

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performTouchInput

fun SemanticsNodeInteraction.performLongClick(): SemanticsNodeInteraction {
    return performLongClickImpl()
}

private fun SemanticsNodeInteraction.performLongClickImpl(): SemanticsNodeInteraction {
    return performTouchInput {
        longClick()
    }
}
