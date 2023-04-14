package com.ignitetech.compose.ui.home

import android.content.Context
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.google.accompanist.navigation.animation.composable
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.extensions.destinationRoute
import com.ignitetech.compose.utility.matchers.hasDrawable
import com.ignitetech.compose.utility.matchers.hasScreen
import com.ignitetech.compose.utility.matchers.withRole
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeScreenTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Before
    fun setUp() {
        hiltTestRule.inject()
    }

    @Test
    fun displaysAllElements() {
        setScreen()

        // Title
        composeTestRule
            .onNode(
                hasText(context.getString(R.string.app_name))
            )
            .assertIsDisplayed()

        // Menu items
        assertIconButtonDisplayed(R.drawable.baseline_search_24, R.string.cd_search_conversation)
        assertIconButtonDisplayed(R.drawable.baseline_archive_24, R.string.cd_archive_chat)

        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(Icons.Default.MoreVert) and
                    hasContentDescription(context.getString(R.string.cd_more_items))
            )
            .assertIsDisplayed()

        // Fab
        assertIconButtonDisplayed(R.drawable.baseline_chat_24, R.string.cd_new_chat)

        // Tabs
        assertTabDisplayed(0, R.string.chats)
        assertTabDisplayed(1, R.string.groups)
        assertTabDisplayed(2, R.string.calls)
    }

    private fun assertIconButtonDisplayed(
        @DrawableRes icon: Int,
        @StringRes contentDescription: Int
    ) {
        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(icon) and
                    hasContentDescription(context.getString(contentDescription))
            )
            .assertIsDisplayed()
    }

    private fun assertTabDisplayed(childIndex: Int, @DrawableRes title: Int) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .onChildAt(childIndex)
            .assert(
                withRole(Role.Tab) and
                    hasText(context.getString(title))
            )
            .assertIsDisplayed()
    }

    @Test
    fun onFabClickShowsToast() {
        setScreen()

        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(R.drawable.baseline_chat_24) and
                    hasContentDescription(context.getString(R.string.cd_new_chat))
            )
            .performClick()

        composeTestRule
            .onNode(hasText(FAB_TOAST_MESSAGE))
            .assertIsDisplayed()
    }

    @Test
    fun onSwitchTabSwitchesTab() {
        setScreen()

        // Chats screen
        composeTestRule
            .onNode(hasScreen(Screens.HomeScreens.Chats))
            .assertIsDisplayed()

        // Groups screen
        composeTestRule
            .onNode(
                withRole(Role.Tab) and
                    hasContentDescription(context.getString(R.string.groups))
            )
            .performClick()

        composeTestRule
            .onNode(hasScreen(Screens.HomeScreens.Groups))
            .assertIsDisplayed()

        // Calls screen
        composeTestRule
            .onNode(
                withRole(Role.Tab) and
                    hasContentDescription(context.getString(R.string.calls))
            )
            .performClick()

        composeTestRule
            .onNode(hasScreen(Screens.HomeScreens.Calls))
            .assertIsDisplayed()
    }

    @Test
    fun onClickSettingsOpensSettings() {
        var navController by Delegates.notNull<TestNavHostController>()

        setScreen { navController = it }

        assertMenuItemDisplayedAndClickDismissesPopUp(R.string.settings)

        // Settings screen
        assertEquals(Screens.Settings.route, navController.destinationRoute)
    }

    @Test
    fun onClickNewGroupOpenNewGroup() {
        var navController by Delegates.notNull<TestNavHostController>()

        setScreen { navController = it }

        assertMenuItemDisplayedAndClickDismissesPopUp(R.string.new_group)

        // Home screen
        assertEquals(Screens.Home.route, navController.destinationRoute)
    }

    private fun assertMenuItemDisplayedAndClickDismissesPopUp(string: Int) {
        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(Icons.Default.MoreVert) and
                    hasContentDescription(context.getString(R.string.cd_more_items))
            )
            .performClick()

        composeTestRule
            .onNode(hasText(context.getString(string)))
            .assert(hasAnyAncestor(isPopup()))
            .performClick()

        composeTestRule
            .onNode(isPopup())
            .assertDoesNotExist()
    }

    @Test
    fun onClickSearchOpensSearch() {
        setScreen()

        // Default state
        assertNoSearchState()

        // Search state
        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(R.drawable.baseline_search_24) and
                    hasContentDescription(context.getString(R.string.cd_search_conversation))
            )
            .performClick()

        composeTestRule
            .onNode(hasText(context.getString(R.string.ph_search)))
            .assertIsDisplayed()

        assertChipDisplayed(R.drawable.baseline_image_24, R.string.photo)
        assertChipDisplayed(R.drawable.baseline_video_file_24, R.string.video)
        assertChipDisplayed(R.drawable.baseline_file_24, R.string.document)

        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(Icons.Default.ArrowBack) and
                    hasContentDescription(context.getString(R.string.cd_back))
            ).performClick()

        // Default state
        assertNoSearchState()
    }

    private fun assertNoSearchState() {
        composeTestRule
            .onNode(hasText(context.getString(R.string.ph_search)))
            .assertDoesNotExist()

        assertChipNotDisplayed(R.drawable.baseline_image_24, R.string.photo)
        assertChipNotDisplayed(R.drawable.baseline_video_file_24, R.string.video)
        assertChipNotDisplayed(R.drawable.baseline_file_24, R.string.document)
    }

    private fun assertChipNotDisplayed(@DrawableRes drawable: Int, @StringRes string: Int) {
        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(drawable) and
                    hasContentDescription(context.getString(string))
            ).assertDoesNotExist()
    }

    private fun assertChipDisplayed(@DrawableRes drawable: Int, @StringRes string: Int) {
        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(drawable) and
                    hasContentDescription(context.getString(string))
            ).assertIsDisplayed()
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun setScreen(navHostAvailable: (TestNavHostController) -> Unit = {}) {
        composeTestRule.activity.setContent {
            val navHostController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(AnimatedComposeNavigator())
                setGraph(
                    createGraph(Screens.Home.route, null) {
                        composable(route = Screens.Home.route) {
                        }
                        composable(route = Screens.Settings.route) {
                        }
                    },
                    null
                )
                navHostAvailable(this)
            }
            TestContainer {
                HomeScreen(navHostController, viewModel = hiltViewModel())
            }
        }
    }
}
