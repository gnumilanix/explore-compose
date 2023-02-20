package com.ignitetech.compose.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ignitetech.compose.chat.ChatScreen
import com.ignitetech.compose.ui.Arguments
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
fun AppNav(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val onboardComplete by viewModel.onboardComplete.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable(route = Routes.Home) {
            HomeScreen(viewModel, navController)
        }
        composable(route = Routes.Chats,
            arguments = listOf(
                navArgument(Arguments.UserId) {
                    type = NavType.IntType
                }
            )
        ) {
            ChatScreen(navController, it.arguments!!.getInt(Arguments.UserId))
        }
    }

    if (!onboardComplete) {
        OnboardScreen {
            viewModel.onboardComplete()
        }
    }
}

