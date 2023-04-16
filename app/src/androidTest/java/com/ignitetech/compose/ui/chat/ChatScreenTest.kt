package com.ignitetech.compose.ui.chat

import android.content.Context
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotSelected
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.testing.TestNavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.R
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.domain.ChatDetail
import com.ignitetech.compose.domain.ChatsByDate
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.actions.performLongClick
import com.ignitetech.compose.utility.matchers.hasDrawable
import com.ignitetech.compose.utility.matchers.withRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import javax.inject.Inject

@OptIn(
    ExperimentalCoroutinesApi::class
)
abstract class ChatScreenTest {

    @get:Rule(order = 100)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @get:Rule(order = 101)
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    open fun setUp() = runTest {
        preferenceRepository.userId(currentUser.id)
        userDao.saveUsers(currentUser, recipient)
    }

    protected fun assertConversationSelected(
        childIndex: Int,
        chat: ChatDetail,
        selected: Boolean
    ) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .assert(
                hasTestTag(chat.direction.name) and
                    hasText(chat.message) and
                    if (selected) isSelected() else isNotSelected()
            )
    }

    protected fun clickOnContextualModeButton(
        @DrawableRes drawable: Int,
        @StringRes contentDescription: Int
    ) {
        composeTestRule.onNode(
            withRole(Role.Button) and
                hasDrawable(drawable) and
                hasContentDescription(context.getString(contentDescription))
        ).performClick()
    }

    protected fun clickOnEditorAction(
        drawable: ImageVector,
        @StringRes contentDescription: Int
    ) {
        composeTestRule.onNode(
            withRole(Role.Button) and
                hasDrawable(drawable) and
                hasContentDescription(context.getString(contentDescription))
        ).performClick()
    }

    protected fun longClickOnConversation(childIndex: Int) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .performLongClick()
    }

    protected fun clickOnConversation(childIndex: Int) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .performClick()
    }

    protected fun assertDateHeader(childIndex: Int, date: String) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .assert(hasText(date))
            .assertIsDisplayed()
    }

    protected fun assertChat(childIndex: Int, chat: ChatDetail, user: User) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .assert(
                hasDrawable(user.avatar) and
                    hasText(user.name) and
                    hasText(chat.message)
            )
            .assertIsDisplayed()
    }

    protected fun assertNoContextualMode() {
        assertToolbarDisplayed()
        assertContextualToolbarNotDisplayed()
    }

    protected fun assertContextualMode(selectedCount: Int) {
        assertToolbarNotDisplayed()
        assertContextualToolbarDisplayed(selectedCount)
    }

    private fun assertContextualToolbarDisplayed(selectedCount: Int) {
        composeTestRule.onNode(
            hasAnySibling(withRole(Role.Image) and hasDrawable(Icons.Filled.ArrowBack)) and
                hasAnySibling(hasText(selectedCount.toString())) and
                hasAnySibling(
                    withRole(Role.Image) and
                        hasDrawable(R.drawable.baseline_delete_24) and
                        hasContentDescription(context.getString(R.string.cd_delete_chat))
                ) and
                hasAnySibling(
                    withRole(Role.Image) and
                        hasDrawable(R.drawable.baseline_content_copy_24) and
                        hasContentDescription(context.getString(R.string.cd_copy_chat))
                )
        )
    }

    private fun assertContextualToolbarNotDisplayed() {
        composeTestRule.onNode(
            withRole(Role.Button) and
                hasDrawable(R.drawable.baseline_delete_24) and
                hasContentDescription(context.getString(R.string.cd_delete_chat))
        ).assertDoesNotExist()
        composeTestRule.onNode(
            withRole(Role.Button) and
                hasDrawable(R.drawable.baseline_content_copy_24) and
                hasContentDescription(context.getString(R.string.cd_delete_chat))
        ).assertDoesNotExist()
    }

    protected fun assertToolbarDisplayed() {
        composeTestRule.onNode(
            hasAnySibling(withRole(Role.Image) and hasDrawable(Icons.Filled.ArrowBack)) and
                hasAnySibling(hasText(recipient.name)) and
                hasAnySibling(
                    withRole(Role.Image) and
                        hasDrawable(recipient.avatar) and
                        hasContentDescription(context.getString(R.string.cd_current_user))
                )
        )
    }

    private fun assertToolbarNotDisplayed() {
        composeTestRule.onNode(
            hasAnySibling(hasText(recipient.name)) and
                hasAnySibling(
                    withRole(Role.Image) and
                        hasDrawable(recipient.avatar) and
                        hasContentDescription(context.getString(R.string.cd_current_user))
                )
        ).assertDoesNotExist()
    }

    protected fun pressBack() {
        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
    }

    protected fun setScreen() {
        composeTestRule.activity.intent.putExtra(ChatViewModel.RecipientId, recipient.id)
        composeTestRule.activity.setContent {
            TestContainer {
                ChatScreen(
                    rememberSystemUiController(),
                    TestNavHostController(LocalContext.current),
                    viewModel = hiltViewModel()
                )
            }
        }
    }

    protected fun setScreen(state: ChatUiState) {
        composeTestRule.activity.intent.putExtra(ChatViewModel.RecipientId, recipient.id)
        composeTestRule.activity.setContent {
            TestContainer {
                ChatScreen(
                    rememberSystemUiController(),
                    TestNavHostController(LocalContext.current),
                    state
                )
            }
        }
    }

    companion object {
        val currentUser = User(1, "John", "http://www.example.com/image1.jpeg")
        val recipient = User(2, "Jane", "http://www.example.com/image2.jpeg")
        val chats = listOf(
            ChatsByDate(
                "yesterday",
                listOf(
                    ChatDetail(
                        2,
                        1,
                        "Hello Jack! How are you today? Can you me those presentations",
                        Direction.SENT,
                        "22/02"
                    ),
                    ChatDetail(
                        3,
                        2,
                        "Hello John! I am good. How about you?",
                        Direction.RECEIVED,
                        "22/02"
                    )
                )
            ),
            ChatsByDate(
                "Today",
                listOf(
                    ChatDetail(
                        1,
                        1,
                        "Got busy yesterday",
                        Direction.SENT,
                        "20/02"
                    )
                )
            )
        )
    }
}
