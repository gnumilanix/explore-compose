package com.ignitetech.compose.ui.chat

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.R
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.theme.ComposeTheme
import com.ignitetech.compose.ui.theme.Pink300
import com.ignitetech.compose.ui.theme.Purple300
import com.ignitetech.compose.ui.theme.Red300
import com.ignitetech.compose.utility.ExcludeFromGeneratedCoverageReport

@Composable
internal fun EmojiSelector() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
            .background(ComposeTheme.colors.secondaryBackgroundColor)
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Text(text = "Emoji", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
internal fun AttachmentSelector() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(70.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
            .background(ComposeTheme.colors.secondaryBackgroundColor)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        item {
            AttachmentButton(
                R.drawable.baseline_file_24,
                R.string.cd_document,
                R.string.document,
                Purple300
            )
        }
        item {
            AttachmentButton(
                R.drawable.baseline_photo_camera_24,
                R.string.cd_camera,
                R.string.camera,
                Red300
            )
        }
        item {
            AttachmentButton(
                R.drawable.baseline_image_24,
                R.string.cd_gallery,
                R.string.gallery,
                Pink300
            )
        }
    }
}

@Composable
private fun AttachmentButton(
    @DrawableRes icon: Int,
    @StringRes contentDescription: Int,
    @StringRes text: Int,
    buttonColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {},
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(buttonColor),
            modifier = Modifier
                .size(60.dp)
                .padding(4.dp)
                .aspectRatio(1.0f)
        ) {
            Icon(
                painterResource(icon),
                stringResource(id = contentDescription),
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            )
        }
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
    }
}

@Composable
@Preview(name = "Light mode")
@ExcludeFromGeneratedCoverageReport
fun ChatScreenAttachmentSelectorPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
            rememberNavController(),
            ChatUiState(
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "John", "https://placekitten.com/200/300"),
                listOf()
            ),
            EditorState.Attachment
        )
    }
}

@Composable
@Preview(name = "Light mode")
@ExcludeFromGeneratedCoverageReport
fun ChatScreenEmojiSelectorPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
            rememberNavController(),
            ChatUiState(
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "John", "https://placekitten.com/200/300"),
                listOf()
            ),
            EditorState.Emoji
        )
    }
}
