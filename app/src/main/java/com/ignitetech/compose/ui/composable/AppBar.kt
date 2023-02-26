package com.ignitetech.compose.ui.composable

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ignitetech.compose.R

@Composable
fun AppBarBackButton(navController: NavController) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
        AppBarBackButtonIcon(navController)
    }
}

@Composable
fun AppBarBackButtonIcon(navController: NavController) {
    IconButton(onClick = { navController.navigateUp() }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.cd_back)
        )
    }
}

@Composable
fun AppBarTitle(title: String, modifier: Modifier) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = modifier
        )
    }
}