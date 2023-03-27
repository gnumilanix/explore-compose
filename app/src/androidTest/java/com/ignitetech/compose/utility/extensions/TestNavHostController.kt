package com.ignitetech.compose.utility.extensions

import androidx.navigation.NavArgument
import androidx.navigation.testing.TestNavHostController

val TestNavHostController.destinationRoute: String?
    get() = currentBackStackEntry?.destination?.route

val TestNavHostController.destinationArguments: Map<String, NavArgument>?
    get() = currentBackStackEntry?.destination?.arguments
