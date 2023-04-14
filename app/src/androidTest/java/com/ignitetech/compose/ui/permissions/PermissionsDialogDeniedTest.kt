package com.ignitetech.compose.ui.permissions

import android.app.Activity
import android.app.Instrumentation
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.test.*
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ignitetech.compose.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
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
        setScreen()

        clickOnPermissionRequest()

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()

        verifiedRationaleDialogDisplayed()
        clickOnDialogButtonWithText(R.string.allow)

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show denied
        clickOnPermissionRequest()

        verifiedDeniedDialogDisplayed()
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

        setScreen()

        clickOnPermissionRequest()

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show rationale
        clickOnPermissionRequest()

        verifiedRationaleDialogDisplayed()
        clickOnDialogButtonWithText(R.string.allow)

        // Show system permissions and don't allow
        clickOnSystemDenyButton()

        // Show denied
        clickOnPermissionRequest()

        verifiedDeniedDialogDisplayed()
        clickOnDialogButtonWithText(R.string.settings)

        // Dismiss rationale
        verifyDialogDismissed()

        intended(intentMatcher)
    }
}
