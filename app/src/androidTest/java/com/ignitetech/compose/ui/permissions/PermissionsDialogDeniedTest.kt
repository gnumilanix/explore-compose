package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.test.*
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.ignitetech.compose.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogDeniedTest1 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    /**
     *  1. Show system permission and deny
     *  2. Show rational and allow
     *  3. Show system permission and deny
     *  4. Show denied dialog and dismiss
     */
    @Test
    fun showsDeniedMessageWhenPermissionDenied() {
        setScreen2()

        clickOnPermissionRequest()

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()

        verifyDialogDisplayed(
            R.string.camera_permission_title,
            R.string.camera_permission_message,
            R.string.allow,
            R.string.deny
        )

        clickOnDialogButtonWithText(R.string.allow)

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show denied
        clickOnPermissionRequest()

        verifyDialogDisplayed(
            R.string.camera_permission_title,
            R.string.camera_permission_message_detail,
            R.string.settings,
            R.string.not_now
        )

        clickOnDialogButtonWithText(R.string.not_now)

        // Dismiss rationale
        verifyDialogDismissed()
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogDeniedTest2 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val intentsTest = IntentsRule()

    @Test
    fun launchesSettingsWhenPermissionDenied() {
        val intentMatcher = allOf(
            hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS),
            hasData(Uri.fromParts("package", instrumentation.targetContext.packageName, null))
        )
        intending(intentMatcher).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                null
            )
        )

        setScreen2()

        clickOnPermissionRequest()

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()

        verifyDialogDisplayed(
            R.string.camera_permission_title,
            R.string.camera_permission_message,
            R.string.allow,
            R.string.deny
        )

        clickOnDialogButtonWithText(R.string.allow)

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show denied
        clickOnPermissionRequest()

        verifyDialogDisplayed(
            R.string.camera_permission_title,
            R.string.camera_permission_message_detail,
            R.string.settings,
            R.string.not_now
        )

        clickOnDialogButtonWithText(R.string.settings)

        // Dismiss rationale
        verifyDialogDismissed()

        intended(intentMatcher)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogDeniedTest3 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val intentsTest = IntentsRule()

    @get:Rule(order = 3)
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var bitmap: Bitmap

    @Test
    fun launchesActivityResultLauncherWhenPermissionGranted() {
        var resultBitmap: Bitmap? = null
        val resultData = Intent().putExtra("data", bitmap)

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, resultData))

        setScreen(
            PreviewPermissionState(
                Manifest.permission.CAMERA,
                PermissionStatus.Granted
            ),
            ActivityResultContracts.RequestPermission()
        ) {
            resultBitmap = it
        }

        // Show rationale
        verifyDialogDismissed()

        // Verify result
        assertEquals(resultBitmap, bitmap)
    }
}
