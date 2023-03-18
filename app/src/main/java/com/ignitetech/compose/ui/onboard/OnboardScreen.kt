package com.ignitetech.compose.ui.onboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.home.HomeViewModel

@Composable
fun OnboardScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    OnboardScreen {
        viewModel.onboardComplete()
        navController.navigate(Screens.Home.route) {
            popUpTo(Screens.Onboard.route) {
                inclusive = true
            }
        }
    }
}

@Composable
fun OnboardScreen(onBoardComplete: () -> Unit = {}) {
    Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            OnboardHeader()
            OnboardBullet(R.drawable.baseline_chat_24, R.string.onboard_chat)
            OnboardBullet(R.drawable.baseline_call_24, R.string.onboard_call)
            OnboardBullet(R.drawable.baseline_groups_24, R.string.onboard_groups)
        }
        Button(
            onClick = onBoardComplete,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
        ) {
            Text(text = stringResource(id = R.string.begin))
        }
    }
}

@Composable
private fun OnboardHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primarySurface)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.onboard_title),
            style = MaterialTheme.typography.h3,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.onboard_message),
            style = MaterialTheme.typography.body1,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        )
    }
}

@Composable
private fun OnboardBullet(@DrawableRes image: Int, @StringRes message: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 0.dp)
    ) {
        Icon(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = message),
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardScreenPreview() {
    OnboardScreen()
}
