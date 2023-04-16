package com.ignitetech.compose.ui.chat

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.domain.ChatDetail
import com.ignitetech.compose.domain.ChatsByDate
import com.ignitetech.compose.ui.composable.UserAvatar
import com.ignitetech.compose.ui.theme.ComposeTheme
import com.ignitetech.compose.utility.ExcludeFromGeneratedCoverageReport

@Composable
fun ConversationsByTime(
    state: ChatUiState,
    contextualModeState: ContextualModeState,
    chatSelected: (Int, Boolean) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        state.chats.forEach { (time, conversations) ->
            item {
                ConversationTime(time)
                Spacer(modifier = Modifier.height(2.dp))
            }
            items(conversations) { conversation ->
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Conversation(
                        state.me,
                        state.recipient,
                        conversation,
                        contextualModeState,
                        chatSelected = chatSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationTime(time: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = time,
                fontSize = 12.sp,
                color = Color(0xff607d8b),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(IntrinsicSize.Max)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Conversation(
    me: User?,
    recipient: User?,
    chat: ChatDetail,
    contextualModeState: ContextualModeState,
    chatSelected: (Int, Boolean) -> Unit
) {
    val id = chat.id
    val surfaceColor by animateColorAsState(
        if (contextualModeState.isSelected(id)) ComposeTheme.colors.secondaryBackgroundColor else Color.Transparent,
        tween(durationMillis = 500, delayMillis = 40, easing = LinearOutSlowInEasing)
    )

    val background = Modifier
        .fillMaxWidth()
        .combinedClickable(
            onClick = {
                if (contextualModeState.inSelectionMode) {
                    val selected = !contextualModeState.isSelected(id)
                    contextualModeState.selected(id, selected)
                    chatSelected(id, selected)
                }
            },
            onLongClick = {
                if (!contextualModeState.inSelectionMode) {
                    contextualModeState.selected(id, true)
                    chatSelected(id, true)
                }
            }
        )
        .background(color = surfaceColor, shape = RoundedCornerShape(4.dp))
        .padding(16.dp, 0.dp, 16.dp, 0.dp)
    when (chat.direction) {
        Direction.SENT -> ConversationSent(background, me, chat)
        Direction.RECEIVED -> ConversationReceived(background, recipient, chat)
    }
}

@Composable
private fun ConversationReceived(
    modifier: Modifier,
    user: User?,
    chat: ChatDetail
) {
    Row(
        modifier = modifier
            .padding(0.dp, 4.dp, 60.dp, 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        UserAvatar(user?.avatar)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.Start) {
            ConversationMessage(user, chat, TextAlign.Start)
        }
    }
}

@Composable
private fun ConversationSent(
    modifier: Modifier,
    user: User?,
    chat: ChatDetail
) {
    Row(
        modifier = modifier
            .padding(60.dp, 4.dp, 0.dp, 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.End) {
            ConversationMessage(user, chat, TextAlign.End)
        }
        Spacer(modifier = Modifier.width(8.dp))
        UserAvatar(user?.avatar)
    }
}

@Composable
private fun ConversationMessage(user: User?, chat: ChatDetail, textAlign: TextAlign) {
    Text(
        text = user?.name ?: "",
        color = Color(0xff43a047),
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        textAlign = textAlign
    )
    Spacer(modifier = Modifier.height(4.dp))
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface,
        elevation = 1.dp
    ) {
        Text(
            text = chat.message,
            style = MaterialTheme.typography.body2,
            maxLines = 4,
            modifier = Modifier.padding(8.dp, 4.dp, 8.dp, 4.dp)
        )
    }
}

@Preview(name = "Light mode")
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
@ExcludeFromGeneratedCoverageReport
fun ConversationSentPreview() {
    Conversation(
        User(1, "Jack", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300"),
        ChatDetail(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            Direction.SENT,
            "22/02",
            User(1, "John", "https://placekitten.com/200/300")
        ),
        ContextualModeState()
    ) { _, _ -> }
}

@Preview(name = "Light mode")
@Composable
@ExcludeFromGeneratedCoverageReport
fun ConversationReceivedPreview() {
    Conversation(
        User(1, "Jack", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300"),
        ChatDetail(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            Direction.RECEIVED,
            "22/02",
            User(1, "John", "https://placekitten.com/200/300")
        ),
        ContextualModeState()
    ) { _, _ -> }
}

@Preview(
    name = "Light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
@ExcludeFromGeneratedCoverageReport
fun ConversationsScreenPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
            rememberNavController(),
            ChatUiState(
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "John", "https://placekitten.com/200/300"),
                listOf(
                    ChatsByDate(
                        "yesterday",
                        listOf(
                            ChatDetail(
                                1,
                                1,
                                "Hello Jack! How are you today? Can you me those presentations",
                                Direction.SENT,
                                "22/02",
                                User(1, "John", "https://placekitten.com/200/300")
                            ),
                            ChatDetail(
                                2,
                                2,
                                "Hello John! I am good. How about you?",
                                Direction.RECEIVED,
                                "22/02",
                                User(2, "Jane", "https://placekitten.com/200/100")
                            )
                        )
                    )
                )
            )
        )
    }
}
