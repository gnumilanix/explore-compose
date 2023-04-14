package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
import com.ignitetech.compose.utility.verifySystemPermissionsDialogDisplayed
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogRationaleTest1 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @Test
    fun showsRationaleWhenPermissionNotGranted() {
        setScreen()

        // Don't allow system permission
        clickOnPermissionRequest()
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()
        verifiedRationaleDialogDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogRationaleTest2 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @Test
    fun denyOnRationaleDismissesDialog() {
        setScreen()

        // Don't allow system permission
        clickOnPermissionRequest()
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()
        clickOnDialogButtonWithText(R.string.deny)

        // Dismiss rationale
        verifyDialogDismissed()
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogRationaleTest3 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @Test
    fun allowOnRationaleShowsSystemPermissionsDialog() {
        setScreen()

        // Don't allow system permission
        clickOnPermissionRequest()
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()
        clickOnDialogButtonWithText(R.string.allow)

        // System dialog
        uiDevice.verifySystemPermissionsDialogDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogRationaleTest4 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @Test
    fun denyOnRationaleSystemPermissionsDialogDeniesPermission() {
        setScreen()

        // Don't allow system permission
        clickOnPermissionRequest()
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()
        clickOnDialogButtonWithText(R.string.allow)

        // Dismiss system dialog
        clickOnSystemDenyButton()

        // Dismiss rationale
        verifyDialogDismissed()

        // Verify permission
        assertEquals(
            PackageManager.PERMISSION_DENIED,
            composeTestRule.activity.checkSelfPermission(Manifest.permission.CAMERA)
        )
    }
}

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class PermissionsDialogRationaleTest5 : PermissionsDialogTest() {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val intentsTest = IntentsRule()

    @get:Rule(order = 3)
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var bitmap: Bitmap

    @Test
    fun allowOnRationaleSystemPermissionsDialogGrantsPermission() {
        val resultData = Intent().putExtra("data", bitmap)
        intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, resultData))

        setScreen()

        // Don't allow system permission
        clickOnPermissionRequest()
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()
        clickOnDialogButtonWithText(R.string.allow)

        // Dismiss system dialog
        clickOnSystemAllowButton()

        // Dismiss rationale
        verifyDialogDismissed()

        // Verify permission
        assertEquals(
            PackageManager.PERMISSION_GRANTED,
            composeTestRule.activity.checkSelfPermission(Manifest.permission.CAMERA)
        )
    }
}
