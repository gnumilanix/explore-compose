package com.ignitetech.compose.utility.extensions

import androidx.navigation.testing.TestNavHostController

val TestNavHostController.destinationRoute: String?
    get() = currentBackStackEntry?.destination?.route
