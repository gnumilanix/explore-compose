package com.ignitetech.compose.utility

import android.app.UiAutomation
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class AdbShellProvider(
    private val uiAutomation: UiAutomation
) {
    operator fun invoke(cmd: String, timeoutInSeconds: Long = 2) {
        Log.i(TAG, "Executing command: $cmd")

        try {
            awaitTermination(
                uiAutomation.executeShellCommand(cmd),
                timeoutInSeconds,
                TimeUnit.SECONDS
            )
        } catch (e: TimeoutException) {
            Log.e(TAG, "Timeout while executing cmd: $cmd")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown exception while executing cmd: $cmd")
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
        private const val TAG = "AdbShellProvider"
    }
}
