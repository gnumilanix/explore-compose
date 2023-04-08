package com.ignitetech.compose.ui.home

import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.extensions.getString
import com.ignitetech.compose.utility.matchers.hasDrawable
import com.ignitetech.compose.utility.matchers.hasScreen
import com.ignitetech.compose.utility.matchers.withRole
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeScreenTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun displaysAllElements() {
        setScreen()

        // Title
        composeTestRule
            .onNode(
                hasText(composeTestRule.getString(R.string.app_name))
            )
            .assertIsDisplayed()

        // Menu items
        assertIconButtonDisplayed(R.drawable.baseline_search_24, R.string.cd_search_conversation)
        assertIconButtonDisplayed(R.drawable.baseline_archive_24, R.string.cd_archive_chat)

        composeTestRule
            .onNode(
                withRole(Role.Button) and
                    hasDrawable(Icons.Default.MoreVert) and
                    hasContentDescription(composeTestRule.getString(R.string.cd_more_items))
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
                    hasContentDescription(composeTestRule.getString(contentDescription))
            )
            .assertIsDisplayed()
    }

    private fun assertTabDisplayed(childIndex: Int, @DrawableRes title: Int) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            .onChildAt(childIndex)
            .assert(
                withRole(Role.Tab) and
                    hasText(composeTestRule.getString(title))
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
                    hasContentDescription(composeTestRule.getString(R.string.cd_new_chat))
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
                    hasContentDescription(composeTestRule.getString(R.string.groups))
            )
            .performClick()

        composeTestRule
            .onNode(hasScreen(Screens.HomeScreens.Groups))
            .assertIsDisplayed()

        // Calls screen
        composeTestRule
            .onNode(
                withRole(Role.Tab) and
                    hasContentDescription(composeTestRule.getString(R.string.calls))
            )
            .performClick()

        composeTestRule
            .onNode(hasScreen(Screens.HomeScreens.Calls))
            .assertIsDisplayed()
    }

    @Test
    fun onClickSettingsOpensSettings() {
        setScreen()

        // Chats screen
        composeTestRule
            .onNode(hasScreen(Screens.HomeScreens.Chats))
            .assertIsDisplayed()
    }

    private fun setScreen() {
        composeTestRule.activity.setContent {
            TestContainer {
                HomeScreen(
                    TestNavHostController(LocalContext.current),
                    viewModel = hiltViewModel()
                )
            }
        }
    }
}
