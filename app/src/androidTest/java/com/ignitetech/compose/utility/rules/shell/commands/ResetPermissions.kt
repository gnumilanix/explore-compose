package com.ignitetech.compose.utility.rules.shell.commands

import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import com.ignitetech.compose.utility.rules.shell.ShellCommand
import com.ignitetech.compose.utility.rules.shell.ShellProvider
import com.ignitetech.compose.utility.rules.shell.ShellProvider.Result.FAILURE
import org.junit.Assert.fail

@TargetApi(value = 23)
class ResetPermissions : ShellCommand {
    override fun invoke(shellProvider: ShellProvider, targetPackage: String) {
        if (Build.VERSION.SDK_INT < 23) return

        try {
            if (FAILURE == shellProvider("pm reset-permissions $targetPackage")) {
                fail("Failed to revoke permissions, see logcat for details")
            }
        } catch (exception: Exception) {
            Log.e(TAG, "An Exception was thrown while revoking permission", exception)
            fail("Failed to revoke permissions, see logcat for details")
        }
    }

    companion object {
        private const val TAG = "PermissionsRevoker"
    }
}
