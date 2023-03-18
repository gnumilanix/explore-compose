package com.ignitetech.compose.ui.onboard

import android.content.Context
import androidx.activity.compose.setContent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.google.accompanist.navigation.animation.composable
import com.ignitetech.compose.R
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.extensions.destinationRoute
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class)
class OnboardScreenTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltTestRule.inject()

        composeTestRule.activity.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(AnimatedComposeNavigator())
                setGraph(createGraph(Screens.Splash.route, null) {
                    composable(route = Screens.Splash.route) {
                    }
                    composable(route = Screens.Home.route) {
                    }
                }, null)
            }
            OnboardScreen(navController, hiltViewModel())
        }
    }

    @Test
    fun displaysAllElements() {
        composeTestRule.onNode(hasText(context.getString(R.string.onboard_title)))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(context.getString(R.string.onboard_message)))
            .assertIsDisplayed()

        composeTestRule.onNode(hasText(context.getString(R.string.onboard_chat)))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(context.getString(R.string.onboard_call)))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(context.getString(R.string.onboard_groups)))
            .assertIsDisplayed()

        composeTestRule.onNode(hasText(context.getString(R.string.begin)))
            .assertIsDisplayed()
    }

    @Test
    fun beginNavigatesToHome() {
        composeTestRule.onNode(hasText(context.getString(R.string.begin)))
            .assertIsEnabled()
            .performClick()

        assertEquals(Screens.Home.route, navController.destinationRoute)
    }
}