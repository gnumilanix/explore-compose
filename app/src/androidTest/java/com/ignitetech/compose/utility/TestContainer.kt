package com.ignitetech.compose.utility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ignitetech.compose.ui.theme.ComposeTheme

@Composable
@Suppress("TestFunctionName")
fun TestContainer(screen: @Composable () -> Unit) {
    ComposeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeTheme.colors.materialColors.background)
        ) {
            screen()
        }
    }
}
