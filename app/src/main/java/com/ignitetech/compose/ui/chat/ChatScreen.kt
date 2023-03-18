package com.ignitetech.compose.ui.chat

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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

@Stable
class ContextualModeState(
    initialSelectedItems: Map<Int, Boolean> = mapOf()
) {
    private var _selectedItems = initialSelectedItems.entries
        .map { it.key to it.value }
        .toMutableStateMap()

    var selectedItems: Map<Int, Boolean> = mapOf()
        get() = _selectedItems
        private set

    var inSelectionMode: Boolean = false
        get() = selectedItems.containsValue(true)
        private set

    fun clearSelection() {
        _selectedItems.clear()
    }

    fun selected(id: Int, selected: Boolean) {
        _selectedItems[id] = selected
    }

    fun isSelected(id: Int): Boolean {
        return _selectedItems.getOrElse(id) { false }
    }
}

@Composable
fun ChatScreen(
    systemUiController: SystemUiController,
    navController: NavController,
    viewModel: ChatViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreen(systemUiController, navController, state)
}

@Composable
fun ChatScreen(
    systemUiController: SystemUiController,
    navController: NavController,
    state: ChatUiState,
    editorState: EditorState = EditorState.None
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val contextualModeState = remember {
        ContextualModeState()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                systemUiController,
                navController,
                contextualModeState,
                state.recipient
            )
        }
    ) { padding ->
        var showSelector by remember {
            mutableStateOf(editorState)
        }

        BackHandler(showSelector is Selector) {
            showSelector = EditorState.None
        }

        BackHandler(contextualModeState.inSelectionMode) {
            contextualModeState.clearSelection()
        }

        if (showSelector != EditorState.Typing) {
            LocalFocusManager.current.clearFocus()
        }

        if (showSelector != EditorState.None) {
            contextualModeState.clearSelection()
        }

        if (contextualModeState.inSelectionMode) {
            showSelector = EditorState.None
        }

        Column(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.weight(1.0f)) {
                Box(modifier = Modifier.weight(1.0f)) {
                    ConversationsByTime(
                        state = state,
                        contextualModeState = contextualModeState,
                        chatSelected = { _, _ -> showSelector = EditorState.None }
                    )

                    if (showSelector is Selector) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        showSelector = EditorState.None
                                    })
                                }
                        )
                    }
                }
                Editor(scaffoldState, scope) {
                    showSelector = it

                    if (it != EditorState.None) {
                        contextualModeState.clearSelection()
                    }
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
            .background(ComposeTheme.colors.secondaryBackgroundColor)
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
    systemUiController: SystemUiController,
    navController: NavController,
    contextualModeState: ContextualModeState,
    user: User?
) {
    val transition =
        updateTransition(contextualModeState.inSelectionMode, label = "inSelectionMode")
    val transitionSpec: @Composable Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Color> =
        { tween(500) }
    val statusBarColor by transition.animateColor(
        label = "statusBarColor",
        transitionSpec = transitionSpec
    ) { isInSelectionMode ->
        if (isInSelectionMode) ComposeTheme.colors.contextualStatusBar else ComposeTheme.colors.statusBar
    }
    val backgroundColor by transition.animateColor(
        label = "actionBarColor",
        transitionSpec = transitionSpec
    ) { isInSelectionMode ->
        if (isInSelectionMode) ComposeTheme.colors.contextualAppBar else ComposeTheme.colors.appBar
    }
    val contentColor by transition.animateColor(
        label = "actionBarContentColor",
        transitionSpec = transitionSpec
    ) { isInSelectionMode ->
        if (isInSelectionMode) ComposeTheme.colors.contextualAppBarContent else ComposeTheme.colors.appBarContent
    }

    systemUiController.setStatusBarColor(color = statusBarColor)

    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (contextualModeState.inSelectionMode) {
                AppBarBackButton(navController, ContentAlpha.medium)
                Text(
                    text = "${contextualModeState.selectedItems.filterValues { it }.size}",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 0.dp)
                        .weight(1.0f)
                )
                IconButton(onClick = { contextualModeState.clearSelection() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = stringResource(id = R.string.cd_delete_chat)
                    )
                }
                IconButton(onClick = { contextualModeState.clearSelection() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        contentDescription = stringResource(id = R.string.cd_copy_chat)
                    )
                }
            } else {
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
            color = ComposeTheme.colors.secondaryBackgroundColor,
            modifier = Modifier
                .weight(1.0f),
            shape = RoundedCornerShape(32.dp)
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
            Icons.Default.Send,
            stringResource(R.string.cd_send_message),
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
            icon,
            contentDescription,
            modifier = Modifier.fillMaxSize(),
            tint = Color.White
        )
    }
}

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
    chat: ChatsUiState.ChatDetail,
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
@Composable
fun AppBarPreview() {
    ComposeTheme {
        AppBar(
            systemUiController = rememberSystemUiController(),
            navController = rememberNavController(),
            contextualModeState = ContextualModeState(),
            user = User(1, "Jack", "https://placekitten.com/200/300")
        )
    }
}

@Preview(name = "Light mode")
@Composable
fun AppBarSelectionModePreview() {
    ComposeTheme {
        AppBar(
            systemUiController = rememberSystemUiController(),
            navController = rememberNavController(),
            contextualModeState = ContextualModeState(),
            user = User(1, "Jack", "https://placekitten.com/200/300")
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
        ),
        ContextualModeState()
    ) { _, _ -> }
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
fun ConversationsScreenPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
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
}

@Preview(name = "Light mode")
@Preview(
    name = "Dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun EditorPreview() {
    ComposeTheme {
        Editor()
    }
}

@Composable
@Preview(name = "Light mode")
fun ChatScreenEmojiSelectorPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
            rememberNavController(),
            ChatUiState(
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "John", "https://placekitten.com/200/300"),
                mapOf()
            ),
            EditorState.Emoji
        )
    }
}

@Composable
@Preview(name = "Light mode")
fun ChatScreenAttachmentSelectorPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
            rememberNavController(),
            ChatUiState(
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "John", "https://placekitten.com/200/300"),
                mapOf()
            ),
            EditorState.Attachment
        )
    }
}
