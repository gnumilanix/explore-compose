package com.ignitetech.compose.ui.composable

import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import com.ignitetech.compose.R
import com.ignitetech.compose.utility.drawableVector

@Composable
fun AppBarBackButton(navController: NavController, alpha: Float = ContentAlpha.high) {
    CompositionLocalProvider(LocalContentAlpha provides alpha) {
        AppBarBackButtonIcon(navController)
    }
}

@Composable
fun AppBarBackButtonIcon(navController: NavController) {
    IconButton(
        onClick = { navController.navigateUp() },
        modifier = Modifier.semantics { drawableVector = Icons.Filled.ArrowBack }
    ) {
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
