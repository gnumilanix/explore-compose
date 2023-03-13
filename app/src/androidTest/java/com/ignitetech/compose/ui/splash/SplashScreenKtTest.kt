@file:OptIn(ExperimentalCoroutinesApi::class)

package com.ignitetech.compose.ui.splash

import androidx.activity.ComponentActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.ignitetech.compose.R
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.SetUpNavGraph
import com.ignitetech.compose.ui.home.HomeViewModel
import com.ignitetech.compose.utility.Constants
import com.ignitetech.compose.utility.extensions.destinationRoute
import com.ignitetech.compose.utility.extensions.waitUntilDoesNotExist
import com.ignitetech.compose.utility.matchers.hasDrawable
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalAnimationApi::class)
class SplashScreenKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val temporaryFolderRule: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { temporaryFolderRule.newFile("${Constants.PREFERENCES_SETTINGS}.preferences_pb") }
    )

    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var homeViewModel: HomeViewModel
    lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        preferenceRepository = PreferenceRepository(testDataStore)
        homeViewModel = HomeViewModel(preferenceRepository)

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(AnimatedComposeNavigator())
            }
            SetUpNavGraph(navController, homeViewModel)
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

        Assert.assertEquals(Screens.Home.route, navController.destinationRoute)
    }

    @Test
    fun navHost_navigatesToOnboardScreen_if_onboardIncomplete() = runTest {
        preferenceRepository.onboardComplete(false)
        composeTestRule.waitUntilDoesNotExist(hasDrawable(R.drawable.ic_launcher_foreground))

        Assert.assertEquals(Screens.Onboard.route, navController.destinationRoute)
    }
}