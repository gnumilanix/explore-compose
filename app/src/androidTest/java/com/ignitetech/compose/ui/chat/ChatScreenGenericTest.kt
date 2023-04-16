package com.ignitetech.compose.ui.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildren
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.utility.matchers.hasDrawable
import com.ignitetech.compose.utility.matchers.hasKey
import com.ignitetech.compose.utility.matchers.hasScreen
import com.ignitetech.compose.utility.matchers.withRole
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class ChatScreenGenericTest : ChatScreenTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @Before
    override fun setUp() = runTest {
        hiltTestRule.inject()
        super.setUp()
    }

    @Test
    fun displaysDefaultElements() = runTest {
        setScreen()

        composeTestRule.onNode(hasScreen(Screens.Chats)).assertIsDisplayed()

        assertToolbarDisplayed()

        composeTestRule
            .onNode(hasAnyChild(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionItemInfo)))
            .assertDoesNotExist()

        composeTestRule
            .onNode(
                hasAnySibling(
                    withRole(Role.Button) and
                        hasDrawable(Icons.Filled.Send) and
                        hasContentDescription(context.getString(R.string.cd_send_message))
                ) and

                    hasAnyChild(
                        hasAnySibling(
                            withRole(Role.Button) and
                                hasDrawable(Icons.Default.Face) and
                                hasContentDescription(context.getString(R.string.cd_emoji))
                        ) and hasAnySibling(
                            withRole(Role.Button) and
                                hasDrawable(Icons.Default.Face) and
                                hasContentDescription(context.getString(R.string.cd_emoji))
                        ) and hasAnySibling(
                            withRole(Role.Button) and
                                hasDrawable(Icons.Default.Add) and
                                hasContentDescription(context.getString(R.string.cd_attach_file))
                        ) and hasAnySibling(
                            withRole(Role.Button) and
                                hasDrawable(Icons.Default.LocationOn) and
                                hasContentDescription(context.getString(R.string.cd_attach_location))
                        ) and hasAnySibling(
                            hasKey(SemanticsActions.SetText) and
                                hasText(context.getString(R.string.ph_message))
                        )
                    )
            )
    }

    @Test
    fun displaysConversations() = runTest {
        val state = ChatUiState(currentUser, recipient, chats)
        setScreen(state)

        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildren()
            .assertCountEquals(5)

        val chatsByDate1 = state.chats[0]
        val chatsByDate2 = state.chats[1]

        assertDateHeader(0, chatsByDate1.date)
        assertChat(1, chatsByDate1.chats[0], currentUser)
        assertChat(2, chatsByDate1.chats[1], recipient)

        assertDateHeader(3, chatsByDate2.date)
        assertChat(4, chatsByDate2.chats[0], currentUser)
    }
}
