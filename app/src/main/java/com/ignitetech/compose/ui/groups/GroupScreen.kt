package com.ignitetech.compose.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ignitetech.compose.ui.Screens.HomeScreens

@Composable
fun GroupScreen(tab: HomeScreens) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFE1FFB1))
    ) {
        Text(text = stringResource(id = tab.name))
    }
}

@Preview(showBackground = true)
@Composable
fun GroupScreenPreview() {
    GroupScreen(tab = HomeScreens.Groups)
}