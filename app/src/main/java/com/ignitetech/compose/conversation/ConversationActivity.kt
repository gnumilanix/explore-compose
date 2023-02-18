package com.ignitetech.compose.conversation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.ignitetech.compose.R
import com.ignitetech.compose.conversation.Direction.*
import com.ignitetech.compose.ui.theme.ComposeTheme
import com.ignitetech.compose.ui.theme.Green50
import com.ignitetech.compose.ui.theme.Grey400
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Content {
                ConversationScreen()
            }
        }
    }
}

@Composable
fun Content(content: @Composable () -> Unit) {
    ComposeTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val user by viewModel.user.collectAsState(null)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { AppBar(user) }
    ) { padding ->
        val conversations by viewModel.conversation.collectAsState()

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
private fun AppBar(user: User?) {
    TopAppBar {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val avatar = user?.avatar
            Image(
                painter = when {
                    avatar.isNullOrEmpty() -> rememberVectorPainter(Icons.Default.Face)
                    else -> rememberAsyncImagePainter(user)
                },
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
                EditorIconButton(Icons.Default.LocationOn, stringResource(R.string.cd_attach_location)) {
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
fun ConversationsByTime(conversations: Map<String, List<Conversation>>, modifier: Modifier) {
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
private fun Conversation(conversation: Conversation) {
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
    when (conversation.direction) {
        SENT -> ConversationSent(background, conversation)
        RECEIVED -> ConversationReceived(background, conversation)
    }


}

@Composable
private fun ConversationReceived(
    modifier: Modifier,
    conversation: Conversation
) {
    Row(
        modifier = modifier
            .padding(4.dp, 4.dp, 60.dp, 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        ConversationAvatar(conversation)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.Start) {
            ConversationMessage(conversation, TextAlign.Start)
        }
    }
}

@Composable
private fun ConversationSent(
    modifier: Modifier,
    conversation: Conversation
) {
    Row(
        modifier = modifier
            .padding(60.dp, 4.dp, 4.dp, 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(modifier = Modifier.weight(1.0f), horizontalAlignment = Alignment.End) {
            ConversationMessage(conversation, TextAlign.End)
        }
        Spacer(modifier = Modifier.width(8.dp))
        ConversationAvatar(conversation)
    }
}

@Composable
private fun ConversationMessage(conversation: Conversation, textAlign: TextAlign) {
    Text(
        text = conversation.sender.name,
        color = Color(0xff43a047),
        style = MaterialTheme.typography.subtitle2,
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
            text = conversation.message,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun ConversationAvatar(conversation: Conversation) {
    Column {
        Image(
            painter = rememberAsyncImagePainter(conversation.sender.avatar),
            contentDescription = stringResource(R.string.cd_user_profile),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.5.dp, Color(0xff76d275), CircleShape)
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
fun GreetingsPreview() {
    Conversation(
        Conversation(
            User("John", "https://placekitten.com/200/300"),
            "Hello Jack! How are you today? Can you me those presentations",
            SENT
        )
    )
}

@Preview(name = "Light mode")
@Composable
fun GreetingsReceivedPreview() {
    Conversation(
        Conversation(
            User("John", "https://placekitten.com/200/300"),
            "Hello Jack! How are you today? Can you me those presentations",
            RECEIVED
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
    ConversationScreen()
}

@Preview
@Composable
fun EditorPreview() {
    Editor()
}

