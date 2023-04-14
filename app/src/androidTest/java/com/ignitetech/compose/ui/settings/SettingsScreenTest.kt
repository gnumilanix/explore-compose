package com.ignitetech.compose.ui.settings

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.ignitetech.compose.R
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.matchers.hasDrawable
import com.ignitetech.compose.utility.matchers.hasScreen
import com.ignitetech.compose.utility.resetPermissions
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(
    ExperimentalCoroutinesApi::class
)
class SettingsScreenTest {
    @get:Rule(order = 0)
    var grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val intentsTest = IntentsRule()

    @get:Rule(order = 3)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 4)
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 5)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @MockK
    lateinit var bitmap: Bitmap

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Before
    fun setUp() {
        hiltTestRule.inject()
    }

    @Test
    fun displaysAllElements() = runTest {
        val user = User(1, "John", "http://www.example.com/1")

        preferenceRepository.userId(1)
        userDao.saveUser(user.id, user.name, user.avatar)

        setScreen()

        composeTestRule.onNode(hasScreen(Screens.Settings))
            .assertIsDisplayed()
        composeTestRule.onNode(hasText(context.getString(R.string.settings)))
            .assertIsDisplayed()

        composeTestRule.onNode(hasDrawable(user.avatar))
            .assertIsDisplayed()
        composeTestRule.onNode(hasDrawable(R.drawable.baseline_photo_camera_24))
            .assertIsDisplayed()

        composeTestRule.onNode(hasText(context.getString(R.string.lorem_ipsum)))
            .assertIsDisplayed()
    }

    @Test
    fun onClickImageWhenImageSelectedUpdatesImage() = runTest {
        val user = User(1, "John", "http://www.example.com/1")

        preferenceRepository.userId(1)
        userDao.saveUser(user.id, user.name, user.avatar)

        setScreen()

        val resultData = Intent().putExtra("data", bitmap)
        val intentMatcher = IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)

        intending(intentMatcher)
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, resultData))

        composeTestRule.onNode(hasDrawable(user.avatar))
            .performClick()

        intended(intentMatcher)

        composeTestRule.onNode(hasDrawable(user.avatar))
            .assertDoesNotExist()
    }

    private fun setScreen() {
        composeTestRule.activity.setContent {
            TestContainer {
                SettingsScreen(
                    TestNavHostController(LocalContext.current),
                    hiltViewModel()
                )
            }
        }
    }

    companion object {
        @AfterClass
        @JvmStatic
        fun tearDown() {
            resetPermissions()
        }
    }
}
