package com.ignitetech.compose.ui.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
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
class ChatScreenContextualModeTest : ChatScreenTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @Before
    override fun setUp() = runTest {
        hiltTestRule.inject()
        super.setUp()
    }

    @Test
    fun toggleConversationSelectionUpdatesContextualState() = runTest {
        val state = ChatUiState(currentUser, recipient, chats)
        setScreen(state)

        // No selection allowed on date headers
        longClickOnConversation(0)
        assertNoContextualMode()

        // Selection on conversations
        longClickOnConversation(1)
        assertConversationSelected(1, state.chats[0].chats[0], true)
        assertContextualMode(1)
        clickOnConversation(2)
        assertContextualMode(2)
        assertConversationSelected(2, state.chats[0].chats[1], true)

        // Revert contextual mode
        clickOnConversation(1)
        assertContextualMode(1)
        assertConversationSelected(1, state.chats[0].chats[0], false)

        clickOnConversation(2)
        assertNoContextualMode()
        assertConversationSelected(2, state.chats[0].chats[1], false)
    }

    @Test
    fun systemBackPressOnContextualStateRevertsContextualMode() = runTest {
        val state = ChatUiState(currentUser, recipient, chats)
        val chat = state.chats[0].chats[0]
        setScreen(state)

        // Switch to contextual mode
        longClickOnConversation(1)
        assertContextualMode(1)
        assertConversationSelected(1, chat, true)

        // Dismiss contextual mode
        pressBack()
        assertConversationSelected(1, chat, false)
        assertNoContextualMode()
    }

    @Test
    fun switchEditorStateOnContextualStateRevertsContextualMode() = runTest {
        val state = ChatUiState(currentUser, recipient, chats)
        val chat = state.chats[0].chats[0]
        setScreen(state)

        // Switch to contextual mode
        longClickOnConversation(1)
        assertContextualMode(1)
        assertConversationSelected(1, chat, true)

        // Dismiss contextual mode
        clickOnEditorAction(Icons.Default.Face, R.string.cd_emoji)
        assertConversationSelected(1, chat, false)
        assertNoContextualMode()
    }

    @Test
    fun deleteOnContextualStateRevertsContextualMode() = runTest {
        val state = ChatUiState(currentUser, recipient, chats)
        val chat = state.chats[0].chats[0]
        setScreen(state)

        // Switch to contextual mode
        longClickOnConversation(1)
        assertContextualMode(1)
        assertConversationSelected(1, chat, true)

        // Dismiss contextual mode
        clickOnContextualModeButton(R.drawable.baseline_delete_24, R.string.cd_delete_chat)
        assertConversationSelected(1, chat, false)
        assertNoContextualMode()
    }

    @Test
    fun copyOnContextualStateRevertsContextualMode() = runTest {
        val state = ChatUiState(currentUser, recipient, chats)
        val chat = state.chats[0].chats[0]
        setScreen(state)

        // Switch to contextual mode
        longClickOnConversation(1)
        assertContextualMode(1)
        assertConversationSelected(1, chat, true)

        // Dismiss contextual mode
        clickOnContextualModeButton(R.drawable.baseline_content_copy_24, R.string.cd_copy_chat)
        assertConversationSelected(1, chat, false)
        assertNoContextualMode()
    }
}
