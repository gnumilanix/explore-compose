package com.ignitetech.compose.ui.groups

import androidx.activity.compose.setContent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.data.group.Group
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.matchers.hasDrawables
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
internal class GroupScreenTest {
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
                GroupScreen(viewModel = hiltViewModel())
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
                GroupScreen(groups)
            }
        }

        composeTestRule
            .onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo) and
                    hasAnyChild(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            )
            .onChildren()
            .assertCountEquals(3)

        assertGroup(0, groups[0])
        assertGroup(1, groups[1])
        assertGroup(2, groups[2])
    }

    private fun assertGroup(childIndex: Int, group: Group) {
        val userAvatars = group.users.map { it.avatar ?: "" }.toTypedArray()

        composeTestRule
            .onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo) and
                    hasAnyChild(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo))
            )
            .onChildAt(childIndex)
            .assert(
                hasDrawables(*userAvatars) and
                    hasText(group.name)
            )
            .assertIsDisplayed()
    }

    private val groups = listOf(
        Group(
            "Friends",
            listOf(
                User(1, "John", "https://placekitten.com/200/300"),
                User(1, "Jack", "https://placekitten.com/200/300")
            )
        ),
        Group(
            "Family",
            listOf(
                User(1, "John", "https://placekitten.com/200/300"),
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "Jane", "https://placekitten.com/200/300")
            )
        ),
        Group(
            "Friends",
            listOf(
                User(1, "John", "https://placekitten.com/200/300"),
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "Jane", "https://placekitten.com/200/300"),
                User(1, "Amy", "https://placekitten.com/200/300"),
                User(1, "Cindy", "https://placekitten.com/200/300"),
                User(1, "Mandy", "https://placekitten.com/200/300")
            )
        )
    )
}
