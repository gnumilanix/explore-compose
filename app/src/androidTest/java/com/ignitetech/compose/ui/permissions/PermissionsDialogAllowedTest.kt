package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.ui.test.*
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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
class PermissionsDialogAllowedTest : PermissionsDialogTest() {
    @get:Rule(order = 0)
    var grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.CAMERA)

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

        setScreen2 { resultBitmap = it }

        // Does not show any dialog
        clickOnPermissionRequest()
        verifyDialogDismissed()

        // Verify result

        assertEquals(bitmap, resultBitmap)
    }
}
