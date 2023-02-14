package com.example.compose

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import coil.compose.rememberAsyncImagePainter
import com.example.compose.ui.theme.ComposeTheme
import com.example.compose.ui.theme.Green50

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
    viewModel: HomeViewModel = viewModel()
) {
    val uiSate by viewModel.conversation.collectAsState()
    ConversationsByTime(uiSate)
}

@Composable
fun ConversationsByTime(conversations: Map<String, List<Conversation>>) {
    LazyColumn(modifier = Modifier.padding(4.dp)) {
        conversations.forEach { (time, conversations) ->
            item {
                ConversationTime(time)
                Spacer(modifier = Modifier.height(2.dp))
            }
            items(conversations) { conversation ->
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isSelected = !isSelected }
            .background(color = surfaceColor, shape = RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(conversation.avatar),
                contentDescription = "User profile",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color(0xff76d275), CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = conversation.sender,
                color = Color(0xff43a047),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
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
            "John",
            "Hello Jack! How are you today? Can you me those presentations",
            "https://placekitten.com/200/300"
        )
    )
}

@Preview(
    name = "Light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true
)
@Composable
fun ConversationsByTimePreview() {
    ConversationsByTime(SampleData.messages)
}

