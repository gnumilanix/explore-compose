package com.ignitetech.compose.ui.chat

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ignitetech.compose.R
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.composable.AppBarBackButton
import com.ignitetech.compose.ui.composable.AppBarTitle
import com.ignitetech.compose.ui.theme.ComposeTheme
import com.ignitetech.compose.utility.ExcludeFromGeneratedCoverageReport
import com.ignitetech.compose.utility.drawableId
import com.ignitetech.compose.utility.drawableUrl
import com.ignitetech.compose.utility.screen

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
    val contextualState = remember {
        ChatContextualState()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                systemUiController,
                navController,
                contextualState,
                state.recipient
            )
        },
        modifier = Modifier.semantics { screen = Screens.Chats }
    ) { padding ->
        var showSelector by remember {
            mutableStateOf(editorState)
        }

        BackHandler(showSelector is Selector) {
            showSelector = EditorState.None
        }

        BackHandler(contextualState.inSelectionMode) {
            contextualState.clearSelection()
        }

        if (showSelector != EditorState.Typing) {
            LocalFocusManager.current.clearFocus()
        }

        if (showSelector != EditorState.None) {
            contextualState.clearSelection()
        }

        if (contextualState.inSelectionMode) {
            showSelector = EditorState.None
        }

        Column(modifier = Modifier.padding(padding)) {
            Column(modifier = Modifier.weight(1.0f)) {
                Box(modifier = Modifier.weight(1.0f)) {
                    ConversationsByTime(
                        state = state,
                        contextualState = contextualState,
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
                        contextualState.clearSelection()
                    }
                }
            }

            AnimatedVisibility(visible = showSelector == EditorState.Emoji) {
                EmojiSelector()
            }
            AnimatedVisibility(visible = showSelector == EditorState.Attachment) {
                AttachmentSelector {
                    showSelector = EditorState.None
                }
            }
        }
    }
}

@Composable
private fun AppBar(
    systemUiController: SystemUiController,
    navController: NavController,
    contextualState: ChatContextualState,
    user: User?
) {
    val transition =
        updateTransition(contextualState.inSelectionMode, label = "inSelectionMode")
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
            if (contextualState.inSelectionMode) {
                AppBarBackButton(navController, ContentAlpha.medium)
                Text(
                    text = "${contextualState.selectedItems.filterValues { it }.size}",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 8.dp, 0.dp)
                        .weight(1.0f)
                )
                IconButton(
                    onClick = { contextualState.clearSelection() },
                    modifier = Modifier.semantics { drawableId = R.drawable.baseline_delete_24 }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = stringResource(id = R.string.cd_delete_chat)
                    )
                }
                IconButton(
                    onClick = { contextualState.clearSelection() },
                    modifier = Modifier.semantics {
                        drawableId = R.drawable.baseline_content_copy_24
                    }
                ) {
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
                        .semantics { drawableUrl = user?.avatar }
                )
                AppBarTitle(title = user?.name ?: "", modifier = Modifier.weight(1.0f))
            }
        }
    }
}

@Preview(name = "Light mode")
@Composable
@ExcludeFromGeneratedCoverageReport
fun AppBarPreview() {
    ComposeTheme {
        AppBar(
            systemUiController = rememberSystemUiController(),
            navController = rememberNavController(),
            contextualState = ChatContextualState(),
            user = User(1, "Jack", "https://placekitten.com/200/300")
        )
    }
}

@Preview(name = "Light mode")
@Composable
@ExcludeFromGeneratedCoverageReport
fun AppBarSelectionModePreview() {
    ComposeTheme {
        AppBar(
            systemUiController = rememberSystemUiController(),
            navController = rememberNavController(),
            contextualState = ChatContextualState(mapOf(1 to true)),
            user = User(1, "Jack", "https://placekitten.com/200/300")
        )
    }
}

@Preview(
    name = "Light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
@ExcludeFromGeneratedCoverageReport
fun ConversationsEmptyScreenPreview() {
    ComposeTheme {
        ChatScreen(
            rememberSystemUiController(),
            rememberNavController(),
            ChatUiState(
                User(1, "Jack", "https://placekitten.com/200/300"),
                User(1, "John", "https://placekitten.com/200/300"),
                listOf()
            )
        )
    }
}
