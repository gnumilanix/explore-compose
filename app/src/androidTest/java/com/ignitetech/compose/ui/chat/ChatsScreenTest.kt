package com.ignitetech.compose.ui.chat

import androidx.activity.compose.setContent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.google.accompanist.navigation.animation.composable
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.domain.ChatDetail
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.extensions.destinationArguments
import com.ignitetech.compose.utility.extensions.destinationRoute
import com.ignitetech.compose.utility.matchers.hasDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(
    ExperimentalAnimationApi::class
)
class ChatsScreenTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun noElementsDisplayedWhenEmpty() {
        composeTestRule.activity.setContent {
            TestContainer {
                ChatsScreen(
                    TestNavHostController(LocalContext.current),
                    viewModel = hiltViewModel()
                )
            }
        }

        composeTestRule
            .onNode(hasAnyChild(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionItemInfo)))
            .assertDoesNotExist()
    }

    @Test
    fun displaysAllElements() {
        composeTestRule.activity.setContent {
            TestContainer {
                ChatsScreen(
                    TestNavHostController(LocalContext.current).apply {
                        navigatorProvider.addNavigator(AnimatedComposeNavigator())
                    },
                    chats
                )
            }
        }

        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildren()
            .assertCountEquals(2)

        assertChat(0, chats[0])
        assertChat(1, chats[1])
    }

    private fun assertChat(childIndex: Int, chatDetail: ChatDetail) {
        val sender = chatDetail.sender!!

        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .assert(
                hasDrawable(sender.avatar) and
                        hasText(sender.name) and
                        hasText(chatDetail.message) and
                        hasText(chatDetail.date)
            )
            .assertIsDisplayed()
    }

    @Test
    fun clickingOnChatNavigatesToChat() {
        var navController by Delegates.notNull<TestNavHostController>()
        val clickedChatIndex = 0
        val chatDetail = chats[clickedChatIndex]
        val sender = chatDetail.sender!!

        composeTestRule.activity.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(AnimatedComposeNavigator())
                setGraph(
                    createGraph(Screens.Chats.route, null) {
                        composable(
                            route = Screens.Chats.route,
                            arguments = listOf(
                                navArgument(ChatViewModel.RecipientId) {
                                    type = NavType.IntType
                                }
                            )
                        ) {}
                    },
                    null
                )
            }
            TestContainer {
                ChatsScreen(navController, chats)
            }
        }
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(clickedChatIndex)
            .performClick()

        assertEquals(Screens.Chats.route, navController.destinationRoute)
        assertNotNull(navController.destinationArguments?.get(ChatViewModel.RecipientId))
        assertEquals(
            sender.id,
            navController.currentBackStackEntry?.arguments?.getInt(ChatViewModel.RecipientId)
        )
    }

    private val chats = listOf(
        ChatDetail(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            Direction.SENT,
            "10:00",
            User(1, "John", "https://placekitten.com/200/300")
        ),
        ChatDetail(
            2,
            2,
            "Hello Jack! How are you today? Can you me those presentations",
            Direction.RECEIVED,
            "Yesterday",
            User(2, "Jane", "https://placekitten.com/200/100")
        )
    )
}