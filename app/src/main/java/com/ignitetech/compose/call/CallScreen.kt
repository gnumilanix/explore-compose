package com.ignitetech.compose.call

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ignitetech.compose.R
import com.ignitetech.compose.conversation.UserAvatar
import com.ignitetech.compose.data.call.Call
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.call.Type.*
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.theme.Green500
import com.ignitetech.compose.ui.theme.Red500
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallScreen(viewModel: CallViewModel = viewModel()) {
    val calls by viewModel.calls.collectAsState()
    CallScreen(calls)
}

@Composable
fun CallScreen(calls: Map<String, List<Call>>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            calls.forEach { (time, calls) ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CallTime(time)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                itemsIndexed(calls) { callIndex, call ->
                    Row {
                        Call(call)
                    }
                }
            }
        }
    }
}

@Composable
private fun CallTime(time: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp, 16.dp, 4.dp)
    ) {
        Row(
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
private fun Call(call: Call) {
    Row(modifier = Modifier
        .clickable { }
        .padding(16.dp, 8.dp, 16.dp, 8.dp)
    ) {
        UserAvatar(call.caller?.avatar)
        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(8.dp, 0.dp, 8.dp, 0.dp)
        ) {
            Text(
                text = call.caller?.name ?: "",
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                CallTypeIcon(call.type)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = SimpleDateFormat(
                        "MMMM dd, HH mm",
                        Locale.getDefault()
                    ).format(call.date.time),
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_call_24),
            contentDescription = stringResource(id = R.string.cd_call),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically),
            tint = Green500
        )
    }
}

@Composable
private fun CallTypeIcon(type: Type) {
    val modifier = Modifier.size(16.dp)
    when (type) {
        INCOMING -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_received_24),
            contentDescription = stringResource(id = R.string.cd_call_incoming),
            tint = Green500,
            modifier = modifier
        )
        OUTGOING -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_made_24),
            contentDescription = stringResource(id = R.string.cd_call_outgoing),
            tint = Green500,
            modifier = modifier
        )
        INCOMING_MISSED -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_missed_24),
            contentDescription = stringResource(id = R.string.cd_call_incoming_missed),
            tint = Red500,
            modifier = modifier
        )
        OUTGOING_MISSED -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_missed_outgoing_24),
            contentDescription = stringResource(id = R.string.cd_call_outgoing_missed),
            tint = Red500,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CallScreenPreview() {
    CallScreen(
        mapOf(
            "Today" to listOf(
                Call(
                    0, 1, 1000, INCOMING,
                    Calendar.getInstance(),
                    User(1, "John", "http://placekitten.com/200/300")
                )
            ),
            "Yesterday" to listOf(
                Call(
                    0, 1, 60000, OUTGOING,
                    Calendar.getInstance(),
                    User(2, "Jane", "http://placekitten.com/200/100")
                ), Call(
                    0, 1, 0, INCOMING_MISSED,
                    Calendar.getInstance(),
                    User(1, "John", "http://placekitten.com/200/300")
                ),
                Call(
                    0, 1, 0, OUTGOING_MISSED,
                    Calendar.getInstance(),
                    User(2, "Jane", "http://placekitten.com/200/100")
                )
            )
        )
    )
}