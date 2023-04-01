package com.ignitetech.compose.ui.splash

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.composable.ShowSystemBars
import com.ignitetech.compose.ui.home.HomeUiState
import com.ignitetech.compose.ui.home.HomeViewModel
import com.ignitetech.compose.utility.ExcludeFromGeneratedCoverageReport
import com.ignitetech.compose.utility.drawableId
import com.ignitetech.compose.utility.screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    systemUiController: SystemUiController,
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SplashScreen(systemUiController, navController, state)
}

@Composable
fun SplashScreen(
    systemUiController: SystemUiController,
    navController: NavHostController,
    state: HomeUiState
) {
    ShowSystemBars(systemUiController, show = false)
    LaunchedEffect(state.onboardComplete) {
        delay(1000) // TODO wait for db seed to complete

        val screen = when (state.onboardComplete) {
            true -> Screens.Home.route
            false -> Screens.Onboard.route
            else -> null
        }

        if (null != screen) {
            navController.navigate(screen) {
                popUpTo(Screens.Splash.route) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .semantics { screen = Screens.Splash }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(192.dp)
                .semantics { drawableId = R.drawable.ic_launcher_foreground },
            tint = MaterialTheme.colors.primary
        )
    }
}

@Preview
@Composable
@ExcludeFromGeneratedCoverageReport
@OptIn(ExperimentalAnimationApi::class)
fun SplashScreenPreview() {
    SplashScreen(rememberSystemUiController(), rememberAnimatedNavController(), HomeUiState())
}
