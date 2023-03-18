package com.ignitetech.compose.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.launch
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.*
import com.ignitetech.compose.R

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun <O> PermissionHandling(
    permissionStateProvider: @Composable (onPermissionResult: (Boolean) -> Unit) -> PermissionState,
    activityResultLauncher: ManagedActivityResultLauncher<Void?, O>,
    @StringRes rationaleTitle: Int,
    @StringRes rationaleMessage: Int,
    @StringRes denialTitle: Int,
    @StringRes denialMessage: Int,
    content: @Composable (permissionHandle: () -> Unit) -> Unit
) {
    var showRationalDialog by remember {
        mutableStateOf(false)
    }
    var showDeniedDialog by remember {
        mutableStateOf(false)
    }
    var permissionAlreadyRequested by remember {
        mutableStateOf(false)
    }
    val permissionState = permissionStateProvider {
        permissionAlreadyRequested = true

        if (it) {
            activityResultLauncher.launch()
        }
    }
    val permissionHandle = {
        if (permissionState.status.isGranted) {
            activityResultLauncher.launch()
        } else if (!permissionAlreadyRequested && !permissionState.status.shouldShowRationale) {
            permissionState.launchPermissionRequest()
        } else if (permissionState.status.shouldShowRationale) {
            showRationalDialog = true
        } else {
            showDeniedDialog = true
        }
    }

    content(permissionHandle)

    if (showRationalDialog) {
        PermissionRationaleDialog(
            title = rationaleTitle,
            message = rationaleMessage,
            onPermissionRequest = { permissionState.launchPermissionRequest() },
            onDismissRequest = { showRationalDialog = false }
        )
    }

    if (showDeniedDialog) {
        PermissionDeniedDialog(
            title = denialTitle,
            message = denialMessage,
            onDismissRequest = { showDeniedDialog = false }
        )
    }
}

@Composable
fun PermissionRationaleDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    onPermissionRequest: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var show by remember { mutableStateOf(true) }

    if (show) {
        AlertDialog(
            onDismissRequest = {
                show = false
                onDismissRequest()
            },
            title = {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(message),
                    style = MaterialTheme.typography.body1
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        show = false
                        onPermissionRequest()
                    },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(
                        text = stringResource(id = R.string.allow),
                        style = MaterialTheme.typography.button
                    )
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.outlinedButtonColors(),
                    onClick = { show = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.deny),
                        style = MaterialTheme.typography.button
                    )
                }
            }
        )
    } else {
        onDismissRequest()
    }
}

@Composable
fun PermissionDeniedDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    onDismissRequest: () -> Unit
) {
    var show by remember { mutableStateOf(true) }
    val context = LocalContext.current

    if (show) {
        AlertDialog(
            onDismissRequest = {
                show = false
                onDismissRequest()
            },
            title = {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(message),
                    style = MaterialTheme.typography.body1
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        show = false
                        context.startApplicationSettings()
                    },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.button
                    )
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.outlinedButtonColors(),
                    onClick = { show = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.not_now),
                        style = MaterialTheme.typography.button
                    )
                }
            }
        )
    } else {
        onDismissRequest()
    }
}

fun Context.startApplicationSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

@Preview
@Composable
fun PermissionRationaleDialogPreview() {
    PermissionRationaleDialog(
        title = R.string.camera_permission_title,
        message = R.string.camera_permission_message,
        onPermissionRequest = {},
        onDismissRequest = {}
    )
}

@Preview
@Composable
fun PermissionDeniedDialogPreview() {
    PermissionDeniedDialog(
        title = R.string.camera_permission_title,
        message = R.string.camera_permission_message_detail,
        onDismissRequest = {}
    )
}

@OptIn(ExperimentalPermissionsApi::class)
class PreviewPermissionState(
    private val _permission: String = Manifest.permission.CAMERA,
    private val _status: PermissionStatus = PermissionStatus.Granted
) : PermissionState {
    override val permission: String
        get() = _permission
    override val status: PermissionStatus
        get() = _status

    override fun launchPermissionRequest() {
    }
}
