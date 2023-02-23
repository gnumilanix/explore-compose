package com.ignitetech.compose.compose

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ignitetech.compose.chat.ChatScreen
import com.ignitetech.compose.chat.ChatViewModel
import com.ignitetech.compose.home.HomeScreen
import com.ignitetech.compose.home.HomeViewModel
import com.ignitetech.compose.home.OnboardScreen
import com.ignitetech.compose.splash.SplashScreen
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.ShowSystemBars

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
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, tween(300))
            }
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
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, tween(500))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, tween(300))
            }
        ) {
            ChatScreen(navController)
        }
    }
}