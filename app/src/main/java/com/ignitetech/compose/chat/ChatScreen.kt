package com.ignitetech.compose.chat

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ignitetech.compose.R
import com.ignitetech.compose.data.chat.Chat
import com.ignitetech.compose.data.chat.Direction.RECEIVED
import com.ignitetech.compose.data.chat.Direction.SENT
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.theme.Green50
import com.ignitetech.compose.ui.theme.Grey400
import com.ignitetech.compose.utility.UserAvatar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ChatScreen(
    navController: NavController,
    userId: Int,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(null)
    val conversations by viewModel.conversation.collectAsState()

    ChatScreen(navController, user, conversations)
}

@Composable
fun ChatScreen(
    navController: NavController,
    user: User?,
    conversations: Map<String, List<Chat>>
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { AppBar(navController, user) }
    ) { padding ->
        Column {
            ConversationsByTime(
                conversations,
                Modifier
                    .padding(padding)
                    .weight(1.0f)
            )
            Editor(scaffoldState, scope)
        }
    }
}

@Composable
private fun AppBar(
    navController: NavController,
    user: User?
) {
    TopAppBar {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.cd_back)
                )
            }
            AsyncImage(
                model = user?.avatar,
                placeholder = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = stringResource(R.string.cd_current_user),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xff76d275), CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Conversations", style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.weight(1.0f)
            )
        }
    }
}

@Composable
private fun Editor(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Grey400,
            modifier = Modifier
                .weight(1.0f),
            shape = RoundedCornerShape(32.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
            ) {
                var message by remember {
                    mutableStateOf("")
                }

                EditorIconButton(Icons.Default.Face, stringResource(R.string.cd_emoji)) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Show emoji")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.ph_message),
                            modifier = Modifier
                                .background(Color.Transparent)
                                .fillMaxWidth()
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1.0f),
                )
                Spacer(modifier = Modifier.width(4.dp))
                EditorIconButton(Icons.Default.Add, stringResource(R.string.cd_attach_file)) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Launch attach")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                EditorIconButton(
                    Icons.Default.LocationOn,
                    stringResource(R.string.cd_attach_location)
                ) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar("Enable location")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        EditorIconButton(Icons.Default.Send, stringResource(R.string.cd_send_message)) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Sending message")
            }
        }
    }
}

@Composable
private fun EditorIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier
            .size(40.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Icon(
            icon, contentDescription, modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ConversationsByTime(conversations: Map<String, List<Chat>>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        conversations.forEach { (time, conversations) ->
            item {
                ConversationTime(time)
                Spacer(modifier = Modifier.height(2.dp))
            }
            items(conversations) { conversation ->
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Conversation(conversation)
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
                modifier = Modifier.width(IntrinsicSize.Max),
            )
        }
    }
}

@Composable
private fun Conversation(chat: Chat) {
    var isSelected by remember {
        mutableStateOf(false)
    }
    val surfaceColor by animateColorAsState(
        if (isSelected) Green50 else Color.Transparent,
        tween(durationMillis = 500, delayMillis = 40, easing = LinearOutSlowInEasing)
    )

    val background = Modifier
        .fillMaxWidth()
        .clickable { isSelected = !isSelected }
        .background(color = surfaceColor, shape = RoundedCornerShape(4.dp))
    when (chat.direction) {
        SENT -> ConversationSent(background, chat)
        RECEIVED -> ConversationReceived(background, chat)
    }
}

@Composable
private fun ConversationReceived(
    modifier: Modifier,
    chat: Chat
) {
    Row(
        modifier = modifier
            .padding(4.dp, 4.dp, 60.dp, 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        UserAvatar(chat.sender?.avatar)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.Start) {
            ConversationMessage(chat, TextAlign.Start)
        }
    }
}

@Composable
private fun ConversationSent(
    modifier: Modifier,
    chat: Chat
) {
    Row(
        modifier = modifier
            .padding(60.dp, 4.dp, 4.dp, 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.End) {
            ConversationMessage(chat, TextAlign.End)
        }
        Spacer(modifier = Modifier.width(8.dp))
        UserAvatar(chat.sender?.avatar)
    }
}

@Composable
private fun ConversationMessage(chat: Chat, textAlign: TextAlign) {
    Text(
        text = chat.sender?.name ?: "",
        color = Color(0xff43a047),
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
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
            maxLines = 1,
            modifier = Modifier.padding(4.dp)
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
fun ConversationSentPreview() {
    Conversation(
        Chat(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            SENT,
            Calendar.getInstance(),
            User(1, "John", "https://placekitten.com/200/300")
        )
    )
}

@Preview(name = "Light mode")
@Composable
fun ConversationReceivedPreview() {
    Conversation(
        Chat(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            RECEIVED,
            Calendar.getInstance(),
            User(1, "John", "https://placekitten.com/200/300")
        )
    )
}

@Preview(
    name = "Light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
fun ConversationsScreenPreview() {
    ChatScreen(
        rememberNavController(),
        User(1, "John", "https://placekitten.com/200/300"),
        mapOf(
            "yesterday" to listOf(
                Chat(
                    1,
                    1,
                    "Hello Jack! How are you today? Can you me those presentations",
                    SENT,
                    Calendar.getInstance(),
                    User(1, "John", "http://placekitten.com/200/300")
                ),
                Chat(
                    2,
                    2,
                    "Hello John! I am good. How about you?",
                    RECEIVED,
                    Calendar.getInstance(),
                    User(2, "Jane", "http://placekitten.com/200/100")
                )
            )
        )
    )
}

@Preview
@Composable
fun EditorPreview() {
    Editor()
}

