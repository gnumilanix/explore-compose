package com.ignitetech.compose.ui.call

import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.domain.CallDetail
import com.ignitetech.compose.domain.CallsByDate
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.matchers.hasDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CallScreenTest {
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
                CallScreen(viewModel = hiltViewModel())
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
                CallScreen(calls)
            }
        }

        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildren()
            .assertCountEquals(6)

        assertDateHeader(0, calls[0].date)
        assertChat(1, calls[0].calls[0], R.drawable.baseline_call_made_24)
        assertChat(2, calls[0].calls[1], R.drawable.baseline_call_missed_24)
        assertChat(3, calls[0].calls[2], R.drawable.baseline_call_missed_outgoing_24)

        assertDateHeader(4, calls[1].date)
        assertChat(5, calls[1].calls[0], R.drawable.baseline_call_received_24)
    }

    private fun assertDateHeader(childIndex: Int, date: String) {
        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .assert(hasText(date))
            .assertIsDisplayed()
    }

    private fun assertChat(childIndex: Int, calls: CallDetail, @DrawableRes typeDrawable: Int) {
        val call = calls
        val target = call.target

        composeTestRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            .onChildAt(childIndex)
            .assert(
                hasDrawable(target.avatar) and
                    hasText(target.name) and
                    hasText(call.date) and
                    hasDrawable(typeDrawable) and
                    hasDrawable(R.drawable.baseline_call_24)
            )
            .assertIsDisplayed()
    }

    private val calls = listOf(
        CallsByDate(
            "Today",
            listOf(
                CallDetail(
                    0,
                    60000,
                    Type.OUTGOING,
                    "February 19, 10:00",
                    User(2, "Jane", "https://placekitten.com/200/100")
                ),
                CallDetail(
                    0,
                    0,
                    Type.INCOMING_MISSED,
                    "February 19, 10:00",
                    User(1, "John", "https://placekitten.com/200/300")
                ),
                CallDetail(
                    0,
                    0,
                    Type.OUTGOING_MISSED,
                    "February 19, 10:00",
                    User(2, "Jane", "https://placekitten.com/200/100")
                )
            )
        ),
        CallsByDate(
            "January 01",
            listOf(
                CallDetail(
                    0,
                    30000,
                    Type.INCOMING,
                    "January 01, 10:00",
                    User(1, "Jane", "https://placekitten.com/200/300")
                )
            )
        )
    )
}
