package com.ignitetech.compose.ui.chat

import androidx.activity.compose.setContent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.matchers.hasScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(
    ExperimentalAnimationApi::class
)
class ChatScreenTest {

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun noElementsDisplayedWhenEmpty() {
        composeTestRule.activity.intent.putExtra(ChatViewModel.RecipientId, 1)
        composeTestRule.activity.setContent {
            TestContainer {
                ChatScreen(
                    rememberSystemUiController(),
                    TestNavHostController(LocalContext.current),
                    viewModel = hiltViewModel()
                )
            }
        }

        composeTestRule.onNode(hasScreen(Screens.Chats))
            .assertIsDisplayed()
        composeTestRule
            .onNode(hasAnyChild(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionItemInfo)))
            .assertDoesNotExist()
    }
}
