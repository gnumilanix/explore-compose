package com.ignitetech.compose.ui.chat

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
import com.ignitetech.compose.utility.matchers.hasDrawable
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
class ChatScreenEditorTest : ChatScreenTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    private val state = ChatUiState(currentUser, recipient, chats)

    @Before
    override fun setUp() = runTest {
        hiltTestRule.inject()
        super.setUp()
        setScreen(state)
    }

    @Test
    fun displaysEmojiWhenClickedAndDismissesOnBackPress() = runTest {
        clickOnEditorAction(Icons.Default.Face, R.string.cd_emoji)
        assertEmojiSelectorDisplayed()

        pressBack()
        assertEmojiSelectorNotDisplayed()
    }

    @Test
    fun typingOnEditorSetsText() = runTest {
        val text = "123456"
        composeTestRule.onNode(hasText(context.getString(R.string.ph_message)))
            .assertIsNotFocused()
            .performClick()
            .assertIsFocused()
            .performTextInput(text)

        composeTestRule.onNode(hasText(text)).assertIsDisplayed()
    }

    @Test
    fun displaysAttachmentWhenClickedAndDismissedOnDocumentClicked() = runTest {
        clickOnEditorAction(Icons.Default.Add, R.string.cd_attach_file)
        assertAttachmentSelectorDisplayed()

        clickOnAttachmentSelector(
            R.drawable.baseline_file_24,
            R.string.cd_document,
            R.string.document
        )
        assertAttachmentSelectorNotDisplayed()
    }

    @Test
    fun displaysAttachmentWhenClickedAndDismissedOnCameraClicked() = runTest {
        clickOnEditorAction(Icons.Default.Add, R.string.cd_attach_file)
        assertAttachmentSelectorDisplayed()

        clickOnAttachmentSelector(
            R.drawable.baseline_photo_camera_24,
            R.string.cd_camera,
            R.string.camera
        )

        assertAttachmentSelectorNotDisplayed()
    }

    @Test
    fun displaysAttachmentWhenClickedAndDismissedOnGalleryClicked() = runTest {
        clickOnEditorAction(Icons.Default.Add, R.string.cd_attach_file)
        assertAttachmentSelectorDisplayed()

        clickOnAttachmentSelector(
            R.drawable.baseline_image_24,
            R.string.cd_gallery,
            R.string.gallery
        )
        assertAttachmentSelectorNotDisplayed()
    }

    @Test
    fun backPressOnAttachmentSelectorDismissesIt() = runTest {
        clickOnEditorAction(Icons.Default.Add, R.string.cd_attach_file)
        assertAttachmentSelectorDisplayed()

        pressBack()
        assertAttachmentSelectorNotDisplayed()
    }

    @Test
    fun clickOnLocationAttachesLocation() = runTest {
        clickOnEditorAction(Icons.Default.LocationOn, R.string.cd_attach_location)

        composeTestRule.onNode(hasText("Enable location")).assertIsDisplayed()
    }

    @Test
    fun clickOnSendSendsMessage() = runTest {
        clickOnEditorAction(Icons.Default.Send, R.string.cd_send_message)

        composeTestRule.onNode(hasText("Sending message")).assertIsDisplayed()
    }

    @Test
    fun clickOnSelectorSwitchesSelector() = runTest {
        clickOnEditorAction(Icons.Default.Add, R.string.cd_attach_file)
        assertAttachmentSelectorDisplayed()

        clickOnEditorAction(Icons.Default.Face, R.string.cd_emoji)
        assertAttachmentSelectorNotDisplayed()
        assertEmojiSelectorDisplayed()

        composeTestRule.onNode(hasText(context.getString(R.string.ph_message)))
            .assertIsNotFocused()
            .performClick()
            .assertIsFocused()
        assertEmojiSelectorNotDisplayed()

        longClickOnConversation(1)
        assertContextualMode(1)
        composeTestRule.onNode(hasText(context.getString(R.string.ph_message)))
            .assertIsNotFocused()
    }

    private fun assertEmojiSelectorDisplayed() {
        composeTestRule.onNode(hasText(context.getString(R.string.cd_emoji))).assertIsDisplayed()
    }

    private fun assertEmojiSelectorNotDisplayed() {
        composeTestRule.onNode(hasText(context.getString(R.string.cd_emoji))).assertDoesNotExist()
    }

    private fun assertAttachmentSelectorDisplayed() {
        composeTestRule.onNode(
            attachmentSemanticsMatcher(
                R.drawable.baseline_file_24,
                R.string.cd_document,
                R.string.document
            ) and attachmentSemanticsMatcher(
                R.drawable.baseline_photo_camera_24,
                R.string.cd_camera,
                R.string.camera
            ) and attachmentSemanticsMatcher(
                R.drawable.baseline_image_24,
                R.string.cd_gallery,
                R.string.gallery
            )
        ).assertIsDisplayed()
    }

    private fun attachmentSemanticsMatcher(
        @DrawableRes icon: Int,
        @StringRes contentDescription: Int,
        @StringRes text: Int
    ) = hasAnyChild(
        hasAnySibling(
            withRole(Role.Button) and
                hasDrawable(icon) and
                hasContentDescription(context.getString(contentDescription))
        ) and hasAnySibling(
            hasText(context.getString(text))
        )
    )

    private fun clickOnAttachmentSelector(
        @DrawableRes icon: Int,
        @StringRes contentDescription: Int,
        @StringRes text: Int
    ) {
        composeTestRule.onNode(
            withRole(Role.Button) and
                hasDrawable(icon) and
                hasContentDescription(context.getString(contentDescription))
        ).performClick()
    }

    private fun assertAttachmentSelectorNotDisplayed() {
        composeTestRule.onNode(
            attachmentSemanticsMatcher(
                R.drawable.baseline_file_24,
                R.string.cd_document,
                R.string.document
            )
        ).assertDoesNotExist()
        composeTestRule.onNode(
            attachmentSemanticsMatcher(
                R.drawable.baseline_photo_camera_24,
                R.string.cd_camera,
                R.string.camera
            ) and attachmentSemanticsMatcher(
                R.drawable.baseline_image_24,
                R.string.cd_gallery,
                R.string.gallery
            )
        ).assertDoesNotExist()
        composeTestRule.onNode(
            attachmentSemanticsMatcher(
                R.drawable.baseline_image_24,
                R.string.cd_gallery,
                R.string.gallery
            )
        ).assertDoesNotExist()
    }
}
