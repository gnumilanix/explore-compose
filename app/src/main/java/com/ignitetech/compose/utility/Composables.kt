package com.ignitetech.compose.utility

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.theme.ComposeTheme

@Composable
fun Content(content: @Composable () -> Unit) {
    ComposeTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun UserAvatar(avatar: String?) {
    Column {
        AsyncImage(
            model = avatar,
            placeholder = painterResource(id = R.drawable.baseline_person_24),
            contentDescription = stringResource(R.string.cd_user_profile),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.5.dp, Color(0xff76d275), CircleShape)
        )
    }
}