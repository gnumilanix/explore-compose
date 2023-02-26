package com.ignitetech.compose.ui.compose

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ignitetech.compose.home.OnboardScreen
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.chat.ChatScreen
import com.ignitetech.compose.ui.chat.ChatViewModel
import com.ignitetech.compose.ui.composable.ShowSystemBars
import com.ignitetech.compose.ui.home.HomeScreen
import com.ignitetech.compose.ui.home.HomeViewModel
import com.ignitetech.compose.ui.settings.SettingsScreen
import com.ignitetech.compose.ui.splash.SplashScreen

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun SetUpNavGraph(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(route = Screens.Splash.route) {
            SplashScreen(viewModel, navController)
        }
        composable(route = Screens.Home.route) {
            ShowSystemBars(show = true)
            HomeScreen(viewModel, navController)
        }
        composable(
            route = Screens.Onboard.route,
            exitTransition = slideOutOfContainerLeft()
        ) {
            ShowSystemBars(show = true)
            OnboardScreen {
                viewModel.onboardComplete()
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Onboard.route) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = Screens.Chats.route,
            arguments = listOf(navArgument(ChatViewModel.RecipientId) {
                type = NavType.IntType
            }),
            enterTransition = slideIntoContainerRight(),
            exitTransition = slideOutOfContainerRight()
        ) {
            ChatScreen(navController)
        }
        composable(
            route = Screens.Settings.route,
            enterTransition = slideIntoContainerRight(),
            exitTransition = slideOutOfContainerRight()
        ) {
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