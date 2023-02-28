package com.ignitetech.compose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Composable
fun ComposeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    ProvideComposeColors(colors) {
        MaterialTheme(
            colors = colors.materialColors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

private val DarkColorPalette = ComposeColors(
    material = darkColors(
        primary = Purple200,
        primaryVariant = Purple700,
        secondary = Teal200,
        surface = Grey900
    ),
    statusBar = Grey900,
    contextualStatusBar = Grey900Primary,
    appBar = Grey900,
    contextualAppBar = Grey900Primary,
    appBarContent = Color.White,
    contextualAppBarContent = Color.White,
    secondaryBackground = Grey900Primary,
    isLight = false
)

private val LightColorPalette = ComposeColors(
    material = lightColors(
        primary = Purple500,
        primaryVariant = Purple700,
        secondary = Teal200
    ),
    statusBar = Purple700,
    contextualStatusBar = Purple700Primary,
    appBar = Purple500,
    contextualAppBar = Purple500Primary,
    appBarContent = Color.White,
    contextualAppBarContent = Color.White,
    secondaryBackground = Grey200,
    isLight = true
)

@Composable
fun ProvideComposeColors(
    colors: ComposeColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalComposeColors provides colors, content = content)
}

private val LocalComposeColors = staticCompositionLocalOf<ComposeColors> {
    error("No LocalComposeColors provided")
}

object ComposeTheme {
    val colors: ComposeColors
        @Composable
        get() = LocalComposeColors.current
}

@Stable
class ComposeColors(
    material: Colors,
    statusBar: Color,
    contextualStatusBar: Color,
    appBar: Color,
    contextualAppBar: Color,
    appBarContent: Color,
    contextualAppBarContent: Color,
    secondaryBackground: Color,
    isLight: Boolean
) {
    var materialColors by mutableStateOf(material)
        private set

    var isLight by mutableStateOf(isLight)
        private set

    var statusBar by mutableStateOf(statusBar)
        private set

    var contextualStatusBar by mutableStateOf(contextualStatusBar)
        private set

    var appBar by mutableStateOf(appBar)
        private set

    var contextualAppBar by mutableStateOf(contextualAppBar)
        private set

    var appBarContent by mutableStateOf(appBarContent)
        private set

    var contextualAppBarContent by mutableStateOf(contextualAppBarContent)
        private set

    var secondaryBackgroundColor by mutableStateOf(secondaryBackground)
        private set
}