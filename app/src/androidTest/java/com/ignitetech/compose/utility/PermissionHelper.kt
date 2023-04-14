package com.ignitetech.compose.utility

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.ignitetech.compose.utility.matchers.withRole

fun resetPermissions() {
    InstrumentationRegistry.getInstrumentation().apply {
        AdbShellProvider(uiAutomation).invoke(
            "pm reset-permissions ${targetContext.packageName}"
        )
    }
}

fun systemAllowText() = when {
    Build.VERSION.SDK_INT == 23 -> "Allow"
    Build.VERSION.SDK_INT <= 28 -> "ALLOW"
    Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
    else -> "While using the app"
}

fun systemDenyText() = when {
    Build.VERSION.SDK_INT in 24..28 -> "DENY"
    Build.VERSION.SDK_INT > 31 -> "Don’t Allow"
    else -> "Deny"
}

fun UiDevice.verifySystemPermissionsDialogDisplayed() {
    hasText("Allow Compose to take pictures and record video?")
    hasText(systemAllowText())
    hasText(systemDenyText())
}

private fun UiDevice.hasText(string: String) {
    findObject(UiSelector().text(string))
}

fun UiDevice.clickOnSystemDenyButton() {
    wait(Until.findObject(By.textContains(systemDenyText())), 1000)
    findObject(UiSelector().textContains(systemDenyText())).click()
}

fun UiDevice.clickOnSystemAllowButton() {
    wait(Until.findObject(By.textContains(systemAllowText())), 1000)
    findObject(UiSelector().textContains(systemAllowText())).click()
}

fun <A : ComponentActivity> verifyDialogDisplayed(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>,
    @StringRes title: Int,
    @StringRes message: Int,
    @StringRes button1: Int,
    @StringRes button2: Int
) {
    val context = composeTestRule.activity

    composeTestRule.onNode(
        isDialog() and

            hasAnyDescendant(
                hasText(context.getString(title))
            ) and
            hasAnyDescendant(
                hasText(context.getString(message))
            ) and
            hasAnyDescendant(
                withRole(Role.Button) and
                    hasText(context.getString(button1))
            ) and
            hasAnyDescendant(
                withRole(Role.Button) and
                    hasText(context.getString(button2))
            )
    ).assertIsDisplayed()
}
