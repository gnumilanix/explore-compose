package com.ignitetech.compose.utility.extensions

import androidx.navigation.NavArgument
import androidx.navigation.testing.TestNavHostController

val TestNavHostController.destinationRoute: String?
    get() = currentDestination?.route

val TestNavHostController.destinationArguments: Map<String, NavArgument>?
    get() = currentDestination?.arguments
