package com.ignitetech.compose.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ignitetech.compose.chat.ChatScreen
import com.ignitetech.compose.chat.ChatViewModel
import com.ignitetech.compose.ui.Routes
import com.ignitetech.compose.utility.Content
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Content {
                AppNav()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AppNav(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = rememberAnimatedNavController()
    val onboardComplete by viewModel.onboardComplete.collectAsState(initial = true)

    AnimatedNavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable(route = Routes.Home) {
            HomeScreen(viewModel, navController)
        }
        composable(
            route = Routes.Chats,
            arguments = listOf(navArgument(ChatViewModel.RecipientId) {
                type = NavType.IntType
            }),
            enterTransition = {
                slideIntoContainer(AnimatedContentScope.SlideDirection.Left, tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, tween(300))
            }
        ) {
            ChatScreen(navController)
        }
    }

    if (!onboardComplete) {
        OnboardScreen {
            viewModel.onboardComplete()
        }
    }
}

