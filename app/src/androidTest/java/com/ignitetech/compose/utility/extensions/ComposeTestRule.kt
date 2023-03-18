package com.ignitetech.compose.utility.extensions

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun <T : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<T>, T>.getString(@StringRes id: Int): String {
    return activity.getString(id)
}

fun <T : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<T>, T>.setContentOnUiThread(
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
    runOnUiThread {
        activity.setContent(parent, content)
    }
}
