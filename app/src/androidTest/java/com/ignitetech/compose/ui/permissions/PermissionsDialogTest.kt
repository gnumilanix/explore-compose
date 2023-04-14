package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.app.Instrumentation
import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.compose.ComposeActivity
import com.ignitetech.compose.utility.*
import com.ignitetech.compose.utility.matchers.withRole
import dagger.hilt.android.qualifiers.ApplicationContext
import org.junit.AfterClass
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
abstract class PermissionsDialogTest {
    @get:Rule(order = Int.MAX_VALUE)
    val composeTestRule = createAndroidComposeRule<ComposeActivity>()

    @Inject
    @ApplicationContext
    lateinit var context: Context

    lateinit var instrumentation: Instrumentation
    lateinit var uiDevice: UiDevice

    @Before
    fun setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation()
        uiDevice = UiDevice.getInstance(instrumentation)
    }

    protected fun setScreen(
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
        uiDevice.clickOnSystemDenyButton()
    }

    protected fun clickOnSystemAllowButton() {
        uiDevice.clickOnSystemAllowButton()
    }

    protected fun verifyDialogDismissed() {
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
    }

    protected fun verifiedRationaleDialogDisplayed() {
        verifyDialogDisplayed(
            composeTestRule,
            R.string.camera_permission_title,
            R.string.camera_permission_message,
            R.string.allow,
            R.string.deny
        )
    }

    protected fun verifiedDeniedDialogDisplayed() {
        verifyDialogDisplayed(
            composeTestRule,
            R.string.camera_permission_title,
            R.string.camera_permission_message_detail,
            R.string.settings,
            R.string.not_now
        )
    }

    protected fun clickOnDialogButtonWithText(@StringRes text: Int) {
        composeTestRule.onNode(
            hasAnyAncestor(isDialog()) and

                withRole(Role.Button) and
                hasText(getString(text))
        ).performClick()
    }

    private fun getString(@StringRes stringRes: Int): String {
        return composeTestRule.activity.getString(stringRes)
    }

    companion object {
        const val BUTTON_TEXT = "Button to test with"

        @AfterClass
        @JvmStatic
        fun tearDown() {
            resetPermissions()
        }
    }
}
