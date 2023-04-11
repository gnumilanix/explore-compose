package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.app.Instrumentation
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.AdbShellProvider
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.extensions.getString
import com.ignitetech.compose.utility.matchers.withRole
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalPermissionsApi::class)
abstract class PermissionsDialogTest {
    @get:Rule(order = Int.MAX_VALUE)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    lateinit var instrumentation: Instrumentation
    lateinit var uiDevice: UiDevice

    @Before
    fun setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation()
        uiDevice = UiDevice.getInstance(instrumentation)
    }

    protected fun systemAllowText() = when {
        Build.VERSION.SDK_INT == 23 -> "Allow"
        Build.VERSION.SDK_INT <= 28 -> "ALLOW"
        Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
        else -> "While using the app"
    }

    protected fun systemDenyText() = when {
        Build.VERSION.SDK_INT in 24..28 -> "DENY"
        Build.VERSION.SDK_INT > 31 -> "Don’t Allow"
        else -> "Deny"
    }

    protected fun setScreen(
        permissionState: PreviewPermissionState,
        requestPermission: ActivityResultContracts.RequestPermission? = null,
        onActivityResult: ((Bitmap?) -> Unit)? = null
    ) {
        composeTestRule.activity.setContent {
            TestContainer {
                val activityResultLauncher = requestPermission?.let {
                    rememberLauncherForActivityResult(requestPermission) {}
                }
                PermissionHandling(
                    permissionStateProvider = {
                        permissionState.apply {
                            launcher = activityResultLauncher
                        }
                    },
                    activityResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicturePreview()
                    ) { onActivityResult?.invoke(it) },
                    rationaleTitle = R.string.camera_permission_title,
                    rationaleMessage = R.string.camera_permission_message,
                    denialTitle = R.string.camera_permission_title,
                    denialMessage = R.string.camera_permission_message_detail
                ) {
                    Button(onClick = { it() }) {
                        Text(text = BUTTON_TEXT)
                    }

                    when (onActivityResult) {
                        null -> it()
                        else -> SideEffect { it() }
                    }
                }
            }
        }
    }

    protected fun setScreen2(
        onActivityResult: ((Bitmap?) -> Unit)? = null
    ) {
        composeTestRule.activity.setContent {
            TestContainer {
                PermissionHandling(
                    permissionStateProvider = { onPermissionResult ->
                        rememberPermissionState(Manifest.permission.CAMERA) {
                            onPermissionResult(
                                it
                            )
                        }
                    },
                    activityResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicturePreview()
                    ) { onActivityResult?.invoke(it) },
                    rationaleTitle = R.string.camera_permission_title,
                    rationaleMessage = R.string.camera_permission_message,
                    denialTitle = R.string.camera_permission_title,
                    denialMessage = R.string.camera_permission_message_detail
                ) {
                    Button(onClick = { it() }) {
                        Text(text = BUTTON_TEXT)
                    }
                }
            }
        }
    }

    protected fun clickOnPermissionRequest() {
        composeTestRule.onNode(withRole(Role.Button) and hasText(BUTTON_TEXT))
            .performClick()
    }

    protected fun clickOnSystemDenyButton() {
        uiDevice.wait(Until.findObject(By.textContains(systemDenyText())), 1000)
        uiDevice.findObject(UiSelector().textContains(systemDenyText())).click()
    }

    protected fun clickOnSystemAllowButton() {
        uiDevice.wait(Until.findObject(By.textContains(systemAllowText())), 1000)
        uiDevice.findObject(UiSelector().textContains(systemAllowText())).click()
    }

    protected fun verifyDialogDismissed() {
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
    }

    protected fun clickOnDialogButtonWithText(@StringRes text: Int) {
        composeTestRule.onNode(
            hasAnyAncestor(isDialog()) and

                withRole(Role.Button) and
                hasText(composeTestRule.getString(text))
        ).performClick()
    }

    protected fun verifyDialogDisplayed(
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes button1: Int,
        @StringRes button2: Int
    ) {
        composeTestRule.onNode(
            isDialog() and

                hasAnyDescendant(
                    hasText(composeTestRule.getString(title))
                ) and
                hasAnyDescendant(
                    hasText(composeTestRule.getString(message))
                ) and
                hasAnyDescendant(
                    withRole(Role.Button) and
                        hasText(composeTestRule.getString(button1))
                ) and
                hasAnyDescendant(
                    withRole(Role.Button) and
                        hasText(composeTestRule.getString(button2))
                )
        ).assertIsDisplayed()
    }

    protected fun UiDevice.hasText(string: String) {
        findObject(UiSelector().text(string))
    }

    companion object {
        const val BUTTON_TEXT = "Button to test with"

        @AfterClass
        @JvmStatic
        fun tearDown() {
            InstrumentationRegistry.getInstrumentation().apply {
                AdbShellProvider(uiAutomation).invoke(
                    "pm reset-permissions ${targetContext.packageName}"
                )
            }
        }
    }
}
