package com.ignitetech.compose.ui.settings

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.composable.AppBarBackButtonIcon
import com.ignitetech.compose.ui.permissions.PermissionHandling
import com.ignitetech.compose.ui.permissions.PreviewPermissionState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreen(navController = navController, permissionState = { onPermissionResult ->
        rememberPermissionState(Manifest.permission.CAMERA) {
            onPermissionResult(
                it
            )
        }
    })
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SettingsScreen(
    navController: NavController,
    permissionState: @Composable (onPermissionResult: (Boolean) -> Unit) -> PermissionState,
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = { AppBarBackButtonIcon(navController = navController) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp, 16.dp, 16.dp, 0.dp)
        ) {
            ProfileImage(permissionState)
            Text(
                text = stringResource(R.string.lorem_ipsum),
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun ProfileImage(
    permissionState: @Composable (onPermissionResult: (Boolean) -> Unit) -> PermissionState,
) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(150.dp)
        ) {
            var image by remember {
                mutableStateOf<Any?>("https://placekitten.com/200/400")
            }
            PermissionHandling(
                permissionState = permissionState,
                activityResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) {
                    image = it
                },
                rationaleTitle = R.string.camera_permission_title,
                rationaleMessage = R.string.camera_permission_message,
                denialTitle = R.string.camera_permission_title,
                denialMessage = R.string.camera_permission_message_detail,
            ) { permissionHandle ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(image).build(),
                    placeholder = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = stringResource(R.string.cd_user_profile),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colors.secondary, CircleShape)
                        .fillMaxSize()
                        .clickable { permissionHandle() }
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.secondary)
                        .clickable { permissionHandle() }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SettingsScreenPreview() {
    SettingsScreen(
        navController = rememberNavController()
    ) { PreviewPermissionState() }
}

@Preview
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun ProfileImagePreview() {
    ProfileImage { PreviewPermissionState() }
}
