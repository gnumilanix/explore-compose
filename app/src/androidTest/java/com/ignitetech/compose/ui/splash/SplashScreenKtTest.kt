package com.ignitetech.compose.ui.splash

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.compose.SetUpNavGraph
import com.ignitetech.compose.ui.home.HomeViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

//TODO Fix test
@OptIn(ExperimentalAnimationApi::class)
@RunWith(MockitoJUnitRunner::class)
class SplashScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var viewModel: HomeViewModel

    lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            SetUpNavGraph(navController, viewModel)
        }
    }

    @Test
    fun appNavHost_verifyStartDestination() {
        composeTestRule.onNode(hasDrawable(R.drawable.ic_launcher_foreground))
            .assertIsDisplayed()
    }
}

val DrawableId = SemanticsPropertyKey<Int>("DrawableResId")
var SemanticsPropertyReceiver.drawableId by DrawableId

fun hasDrawable(@DrawableRes id: Int): SemanticsMatcher =
    SemanticsMatcher.expectValue(DrawableId, id)