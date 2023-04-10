package com.ignitetech.compose.utility.rules.shell

import android.app.UiAutomation
import android.os.ParcelFileDescriptor
import android.util.Log
import com.ignitetech.compose.utility.rules.shell.ShellProvider.Result
import com.ignitetech.compose.utility.rules.shell.ShellProvider.Result.FAILURE
import com.ignitetech.compose.utility.rules.shell.ShellProvider.Result.SUCCESS
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

interface ShellProvider {
    operator fun invoke(cmd: String): Result

    enum class Result {
        SUCCESS, FAILURE
    }
}

/**
 * Extension of [androidx.test.runner.permission.UiAutomationShellCommand] that simplifies invocation
 */
class AdbShellProvider(
    private val uiAutomation: UiAutomation
) : ShellProvider {
    override operator fun invoke(cmd: String): Result {
        Log.i(TAG, "Executing command: $cmd")
        return try {
            awaitTermination(uiAutomation.executeShellCommand(cmd), 2, TimeUnit.SECONDS)
            SUCCESS
        } catch (e: TimeoutException) {
            Log.e(TAG, "Timeout while executing cmd: $cmd")
            FAILURE
        }
    }

    @Throws(IOException::class, TimeoutException::class)
    private fun awaitTermination(
        pfDescriptor: ParcelFileDescriptor,
        timeout: Long,
        unit: TimeUnit
    ) {
        val timeoutInMillis = unit.toMillis(timeout)
        val endTimeInMillis = when {
            timeoutInMillis > 0 -> System.currentTimeMillis() + timeoutInMillis
            else -> 0
        }
        BufferedReader(InputStreamReader(ParcelFileDescriptor.AutoCloseInputStream(pfDescriptor))).use { reader ->
            while (reader.readLine() != null) {
                if (endTimeInMillis > System.currentTimeMillis()) {
                    throw TimeoutException()
                }
            }
        }
    }

    companion object {
        private const val TAG = "ShellCommand"
    }
}
