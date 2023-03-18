package com.ignitetech.compose.ui.splash

import android.content.Context
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.ignitetech.compose.R
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.ui.compose.SetUpNavGraph
import com.ignitetech.compose.ui.home.HomeViewModel
import com.ignitetech.compose.utility.extensions.deleteDatastore
import com.ignitetech.compose.utility.extensions.destinationRoute
import com.ignitetech.compose.utility.extensions.waitUntilDoesNotExist
import com.ignitetech.compose.utility.matchers.hasDrawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class)
class SplashScreenTest {
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
            }
            SetUpNavGraph(navController, composeTestRule.activity.viewModels<HomeViewModel>().value)
        }
    }

    @Test
    fun navHost_startDestination_is_splashScreen() = runTest {
        composeTestRule.onNode(hasDrawable(R.drawable.ic_launcher_foreground))
            .assertIsDisplayed()
    }

    @Test
    fun navHost_navigatesToHomeScreen_if_onboardComplete() = runTest {
        preferenceRepository.onboardComplete(true)
        composeTestRule.waitUntilDoesNotExist(hasDrawable(R.drawable.ic_launcher_foreground))

        assertEquals(Screens.Home.route, navController.destinationRoute)
    }

    @Test
    fun navHost_navigatesToOnboardScreen_if_onboardIncomplete() = runTest {
        preferenceRepository.onboardComplete(false)
        composeTestRule.waitUntilDoesNotExist(hasDrawable(R.drawable.ic_launcher_foreground))

        assertEquals(Screens.Onboard.route, navController.destinationRoute)
    }

    @After
    fun teardown() {
        context.deleteDatastore()
    }
}
