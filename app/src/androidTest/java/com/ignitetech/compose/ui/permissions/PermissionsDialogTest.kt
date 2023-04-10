@file:OptIn(ExperimentalTestApi::class)

package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.app.Instrumentation
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.TestContainer
import com.ignitetech.compose.utility.extensions.getString
import com.ignitetech.compose.utility.matchers.withRole
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPermissionsApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogTest {

//    @get:Rule(order = 0)
//    val resetPermissionRule = ShellCommandRule(ResetPermissions())

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    lateinit var instrumentation: Instrumentation
    lateinit var uiDevice: UiDevice

    @Before
    fun setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation()
        uiDevice = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun showsRationaleWhenPermissionNotGranted() {
        setScreen(
            PreviewPermissionState(
                Manifest.permission.CAMERA,
                PermissionStatus.Denied(true)
            )
        )

        // Show rationale
        composeTestRule.onNode(
            isDialog() and

                hasAnyDescendant(
                    hasText(composeTestRule.getString(R.string.camera_permission_title))
                ) and
                hasAnyDescendant(
                    hasText(composeTestRule.getString(R.string.camera_permission_message))
                ) and
                hasAnyDescendant(
                    withRole(Role.Button) and
                        hasText(composeTestRule.getString(R.string.allow))
                ) and
                hasAnyDescendant(
                    withRole(Role.Button) and
                        hasText(composeTestRule.getString(R.string.deny))
                )
        ).assertIsDisplayed()
    }

    @Test
    fun denyOnRationaleDismissesDialog() {
        setScreen(
            PreviewPermissionState(
                Manifest.permission.CAMERA,
                PermissionStatus.Denied(true)
            )
        )

        // Show rationale
        composeTestRule.onNode(
            hasAnyAncestor(isDialog()) and

                withRole(Role.Button) and
                hasText(composeTestRule.getString(R.string.deny))
        ).performClick()

        // Dismiss rationale
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun allowOnRationaleShowsSystemPermissionsDialog() {
        setScreen(
            PreviewPermissionState(
                Manifest.permission.CAMERA,
                PermissionStatus.Denied(true)
            )
        )

        composeTestRule.onNode(
            hasAnyAncestor(isDialog()) and

                withRole(Role.Button) and
                hasText(composeTestRule.getString(R.string.allow))
        ).performClick()

        // System dialog
        uiDevice.hasText("Allow Compose to take pictures and record video?")
        uiDevice.hasText(
            when {
                Build.VERSION.SDK_INT == 23 -> "Allow"
                Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                else -> "While using the app"
            }
        )
        uiDevice.hasText(
            when (Build.VERSION.SDK_INT) {
                in 24..28 -> "DENY"
                else -> "Deny"
            }
        )
    }

    private fun setScreen(permissionState: PreviewPermissionState) {
        composeTestRule.activity.setContent {
            TestContainer {
                PermissionHandling(
                    permissionStateProvider = { permissionState },
                    activityResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicturePreview()
                    ) { },
                    rationaleTitle = R.string.camera_permission_title,
                    rationaleMessage = R.string.camera_permission_message,
                    denialTitle = R.string.camera_permission_title,
                    denialMessage = R.string.camera_permission_message_detail,
                    content = { it() }
                )
            }
        }
    }

    private fun UiDevice.hasText(string: String) {
        findObject(UiSelector().text(string))
    }
}
