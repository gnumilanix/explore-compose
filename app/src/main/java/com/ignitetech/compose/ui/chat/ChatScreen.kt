package com.ignitetech.compose.ui.chat

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ignitetech.compose.R
import com.ignitetech.compose.data.chat.Direction.RECEIVED
import com.ignitetech.compose.data.chat.Direction.SENT
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.composable.AppBarBackButton
import com.ignitetech.compose.ui.composable.AppBarTitle
import com.ignitetech.compose.ui.composable.UserAvatar
import com.ignitetech.compose.ui.theme.*
import com.ignitetech.compose.utility.isActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private interface Selector

sealed class EditorState {

    object None : EditorState()
    object Typing : EditorState()
    object Emoji : EditorState(), Selector
    object Attachment : EditorState(), Selector
}

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreen(navController, state)
}

@Composable
fun ChatScreen(
    navController: NavController,
    state: ChatUiState,
    editorState: EditorState = EditorState.None
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { AppBar(navController, state.recipient) }
    ) { padding ->
        var showSelector by remember {
            mutableStateOf(editorState)
        }
        var dismissActions by remember {
            mutableStateOf(false)
        }

        BackHandler(showSelector is Selector) {
            showSelector = EditorState.None
        }

        if (showSelector != EditorState.Typing) {
            LocalFocusManager.current.clearFocus()
        }

        Column(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.weight(1.0f)) {
                Box(modifier = Modifier.weight(1.0f)) {
                    ConversationsByTime(state)

                    if (showSelector is Selector) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    showSelector = EditorState.None
                                })
                            })
                    }
                }
                Editor(scaffoldState, scope) {
                    showSelector = it
                    dismissActions = it != EditorState.None
                }
            }

            AnimatedVisibility(visible = showSelector == EditorState.Emoji) {
                EmojiSelector()
            }
            AnimatedVisibility(visible = showSelector == EditorState.Attachment) {
                AttachmentSelector()
            }
        }
    }
}

@Composable
private fun EmojiSelector() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
            .background(secondaryBackgroundColor())
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Text(text = "Emoji", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun AttachmentSelector() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(70.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
            .background(secondaryBackgroundColor())
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
            colors = buttonColors(buttonColor),
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
private fun AppBar(
    navController: NavController,
    user: User?
) {
    TopAppBar {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBarBackButton(navController)
            AsyncImage(
                model = user?.avatar,
                placeholder = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = stringResource(R.string.cd_current_user),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp, 0.dp, 8.dp, 0.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xff76d275), CircleShape)
            )
            AppBarTitle(title = user?.name ?: "", modifier = Modifier.weight(1.0f))
        }
    }
}

@Composable
private fun Editor(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    editorStateChange: (EditorState) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = secondaryBackgroundColor(),
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
                    editorStateChange(EditorState.Emoji)
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
                        .weight(1.0f)
                        .navigationBarsPadding()
                        .imePadding()
                        .onFocusChanged {
                            if (it.isActive) {
                                editorStateChange(EditorState.Typing)
                            }
                        }
                )
                Spacer(modifier = Modifier.width(4.dp))
                EditorIconButton(Icons.Default.Add, stringResource(R.string.cd_attach_file)) {
                    editorStateChange(EditorState.Attachment)
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
        EditorIconButton(
            Icons.Default.Send, stringResource(R.string.cd_send_message),
            PaddingValues(8.dp)
        ) {
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
    contentPadding: PaddingValues = PaddingValues(4.dp),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier
            .size(40.dp),
        contentPadding = contentPadding,
        colors = buttonColors(Purple500)
    ) {
        Icon(
            icon, contentDescription, modifier = Modifier.fillMaxSize(), tint = Color.White
        )
    }
}

@Composable
fun ConversationsByTime(
    state: ChatUiState
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
                    Conversation(state.me, state.recipient, conversation)
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
fun Conversation(
    me: User?,
    recipient: User?,
    chat: ChatsUiState.ChatDetail
) {
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
        .padding(16.dp, 0.dp, 16.dp, 0.dp)
    when (chat.direction) {
        SENT -> ConversationSent(background, me, chat)
        RECEIVED -> ConversationReceived(background, recipient, chat)
    }
}

@Composable
private fun ConversationReceived(
    modifier: Modifier,
    user: User?,
    chat: ChatsUiState.ChatDetail
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
    chat: ChatsUiState.ChatDetail
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
private fun ConversationMessage(user: User?, chat: ChatsUiState.ChatDetail, textAlign: TextAlign) {
    Text(
        text = user?.name ?: "",
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
            maxLines = 4,
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
        User(1, "Jack", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300"),
        ChatsUiState.ChatDetail(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            SENT,
            "22/02",
            User(1, "John", "https://placekitten.com/200/300")
        )
    )
}

@Preview(name = "Light mode")
@Composable
fun ConversationReceivedPreview() {
    Conversation(
        User(1, "Jack", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300"),
        ChatsUiState.ChatDetail(
            1,
            1,
            "Hello Jack! How are you today? Can you me those presentations",
            RECEIVED,
            "22/02",
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
        ChatUiState(
            User(1, "Jack", "https://placekitten.com/200/300"),
            User(1, "John", "https://placekitten.com/200/300"),
            mapOf(
                "yesterday" to listOf(
                    ChatsUiState.ChatDetail(
                        1,
                        1,
                        "Hello Jack! How are you today? Can you me those presentations",
                        SENT,
                        "22/02",
                        User(1, "John", "https://placekitten.com/200/300")
                    ),
                    ChatsUiState.ChatDetail(
                        2,
                        2,
                        "Hello John! I am good. How about you?",
                        RECEIVED,
                        "22/02",
                        User(2, "Jane", "https://placekitten.com/200/100")
                    )
                )
            )
        )
    )
}

@Preview(name = "Light mode")
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun EditorPreview() {
    Editor()
}

@Composable
@Preview(name = "Light mode")
fun ChatScreenEmojiSelectorPreview() {
    ChatScreen(
        rememberNavController(),
        ChatUiState(
            User(1, "Jack", "https://placekitten.com/200/300"),
            User(1, "John", "https://placekitten.com/200/300"),
            mapOf()
        ),
        EditorState.Emoji
    )
}

@Composable
@Preview(name = "Light mode")
fun ChatScreenAttachmentSelectorPreview() {
    ChatScreen(
        rememberNavController(),
        ChatUiState(
            User(1, "Jack", "https://placekitten.com/200/300"),
            User(1, "John", "https://placekitten.com/200/300"),
            mapOf()
        ),
        EditorState.Attachment
    )
}

