package com.ignitetech.compose.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ignitetech.compose.utility.UserAvatar
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User

@Composable
fun ChatsScreen(viewModel: ChatsViewModel = viewModel()) {
    val chats by viewModel.chats.collectAsState()
    ChatsScreen(chats)
}

@Composable
fun ChatsScreen(chats: List<ChatUiState>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chats) { chat ->
            Row(modifier = Modifier
                .clickable { }
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
            ) {
                UserAvatar(chat.sender?.avatar)
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(8.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Row {
                        Text(
                            text = chat.sender?.name ?: "",
                            style = MaterialTheme.typography.subtitle2,
                            maxLines = 1,
                            modifier = Modifier.weight(1.0f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = chat.date,
                            style = MaterialTheme.typography.overline
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chat.message,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatsScreenPreview() {
    ChatsScreen(
        listOf(
            ChatUiState(
                1,
                1,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT,
                "10:00",
                User(1, "John", "http://placekitten.com/200/300")
            ),
            ChatUiState(
                2,
                2,
                "Hello Jack! How are you today? Can you me those presentations",
                Direction.SENT,
                "Yesterday",
                User(2, "Jane", "http://placekitten.com/200/100")
            )
        )
    )
}