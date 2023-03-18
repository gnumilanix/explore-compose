package com.ignitetech.compose.utility.matchers

import androidx.annotation.DrawableRes
import androidx.compose.ui.test.SemanticsMatcher
import com.ignitetech.compose.utility.DrawableId

fun hasDrawable(@DrawableRes id: Int): SemanticsMatcher =
    SemanticsMatcher.expectValue(DrawableId, id)
