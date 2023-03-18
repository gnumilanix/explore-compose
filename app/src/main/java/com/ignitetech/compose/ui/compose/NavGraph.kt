package com.ignitetech.compose.ui.compose

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.chat.ChatScreen
import com.ignitetech.compose.ui.chat.ChatViewModel
import com.ignitetech.compose.ui.composable.ShowSystemBars
import com.ignitetech.compose.ui.home.HomeScreen
import com.ignitetech.compose.ui.home.HomeViewModel
import com.ignitetech.compose.ui.onboard.OnboardScreen
import com.ignitetech.compose.ui.settings.SettingsScreen
import com.ignitetech.compose.ui.splash.SplashScreen

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun SetUpNavGraph(navController: NavHostController, viewModel: HomeViewModel) {
    val systemUiController = rememberSystemUiController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(route = Screens.Splash.route) {
            SplashScreen(systemUiController, navController, viewModel)
        }
        composable(route = Screens.Home.route) {
            ShowSystemBars(systemUiController, show = true)
            HomeScreen(navController, viewModel)
        }
        composable(
            route = Screens.Onboard.route,
            exitTransition = slideOutOfContainerLeft()
        ) {
            ShowSystemBars(systemUiController, show = true)
            OnboardScreen(navController, viewModel)
        }
        composable(
            route = Screens.Chats.route,
            arguments = listOf(navArgument(ChatViewModel.RecipientId) {
                type = NavType.IntType
            }),
            enterTransition = slideIntoContainerRight(),
            exitTransition = slideOutOfContainerRight()
        ) {
            ChatScreen(systemUiController, navController, hiltViewModel())
        }
        composable(
            route = Screens.Settings.route,
            enterTransition = slideIntoContainerRight(),
            exitTransition = slideOutOfContainerRight()
        ) {
            ShowSystemBars(systemUiController, show = true)
            SettingsScreen(navController, viewModel = hiltViewModel())
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutOfContainerLeft(): AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
fun slideOutOfContainerRight(): AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition? = {
    slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
fun slideIntoContainerRight(): AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition? = {
    slideIntoContainer(AnimatedContentScope.SlideDirection.Left, tween(500))
}