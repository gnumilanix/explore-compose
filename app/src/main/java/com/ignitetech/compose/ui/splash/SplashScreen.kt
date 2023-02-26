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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.composable.ShowSystemBars
import com.ignitetech.compose.ui.home.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: HomeViewModel, navController: NavHostController) {
    val onboardComplete by viewModel.onboardComplete.collectAsState(initial = true)

    ShowSystemBars(show = false)
    LaunchedEffect(key1 = true) {
        //TODO wait for db seed to complete
        delay(1000)

        navController.navigate(if (onboardComplete) Screens.Home.route else Screens.Onboard.route) {
            popUpTo(Screens.Splash.route) {
                inclusive = true
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(192.dp),
            tint = MaterialTheme.colors.primary
        )
    }
}

@Preview
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun SplashScreenPreview() {
    SplashScreen(hiltViewModel(), rememberAnimatedNavController())
}