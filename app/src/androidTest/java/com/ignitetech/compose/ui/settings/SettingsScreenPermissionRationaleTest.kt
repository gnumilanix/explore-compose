package com.ignitetech.compose.ui.settings

import android.app.Instrumentation
import android.content.Context
import androidx.activity.compose.setContent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.ignitetech.compose.R
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.*
import com.ignitetech.compose.utility.matchers.hasDrawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
class SettingsScreenPermissionRationaleTest {
    @get:Rule(order = 0)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    lateinit var instrumentation: Instrumentation
    lateinit var uiDevice: UiDevice

    @Before
    fun setUp() {
        hiltTestRule.inject()
        instrumentation = InstrumentationRegistry.getInstrumentation()
        uiDevice = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun onClickImageWhenMissingPermissionShowsRationale() = runTest {
        val user = User(1, "John", "http://www.example.com/1")

        preferenceRepository.userId(1)
        userDao.saveUser(user.id, user.name, user.avatar)

        setScreen()

        composeTestRule.onNode(hasDrawable(user.avatar))
            .performClick()

        // Show system permissions and don't allow
        uiDevice.verifySystemPermissionsDialogDisplayed()
        uiDevice.clickOnSystemDenyButton()

        // Show rationale
        composeTestRule.onNode(hasDrawable(user.avatar))
            .performClick()
        verifyDialogDisplayed(
            composeTestRule,
            R.string.camera_permission_title,
            R.string.camera_permission_message,
            R.string.allow,
            R.string.deny
        )
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
