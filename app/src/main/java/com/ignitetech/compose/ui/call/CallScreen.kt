package com.ignitetech.compose.ui.call

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignitetech.compose.R
import com.ignitetech.compose.data.call.Type
import com.ignitetech.compose.data.call.Type.*
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.domain.CallDetail
import com.ignitetech.compose.domain.CallsByDate
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.composable.UserAvatar
import com.ignitetech.compose.ui.theme.Green500
import com.ignitetech.compose.ui.theme.Red500
import com.ignitetech.compose.utility.ExcludeFromGeneratedCoverageReport
import com.ignitetech.compose.utility.drawableId
import com.ignitetech.compose.utility.screen

@Composable
fun CallScreen(viewModel: CallViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CallScreen(state.calls)
}

@Composable
fun CallScreen(calls: List<CallsByDate>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .semantics { screen = Screens.HomeScreens.Calls }
    ) {
        calls.forEach { (time, calls) ->
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CallTime(time)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(calls) { call ->
                Row {
                    Call(call)
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
                modifier = Modifier.width(IntrinsicSize.Max)
            )
        }
    }
}

@Composable
private fun Call(call: CallDetail) {
    Row(
        modifier = Modifier
            .clickable { }
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
    ) {
        UserAvatar(call.target.avatar)
        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(8.dp, 0.dp, 8.dp, 0.dp)
        ) {
            Text(
                text = call.target.name,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                CallTypeIcon(call.type)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = call.date,
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.baseline_call_24),
            contentDescription = stringResource(id = R.string.cd_call),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
                .semantics {
                    drawableId = R.drawable.baseline_call_24
                },
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
            modifier = modifier.semantics {
                drawableId = R.drawable.baseline_call_received_24
            }
        )
        OUTGOING -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_made_24),
            contentDescription = stringResource(id = R.string.cd_call_outgoing),
            tint = Green500,
            modifier = modifier.semantics {
                drawableId = R.drawable.baseline_call_made_24
            }
        )
        INCOMING_MISSED -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_missed_24),
            contentDescription = stringResource(id = R.string.cd_call_incoming_missed),
            tint = Red500,
            modifier = modifier.semantics {
                drawableId = R.drawable.baseline_call_missed_24
            }
        )
        OUTGOING_MISSED -> Icon(
            painter = painterResource(id = R.drawable.baseline_call_missed_outgoing_24),
            contentDescription = stringResource(id = R.string.cd_call_outgoing_missed),
            tint = Red500,
            modifier = modifier.semantics {
                drawableId = R.drawable.baseline_call_missed_outgoing_24
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
@ExcludeFromGeneratedCoverageReport
fun CallScreenPreview() {
    CallScreen(
        listOf(
            CallsByDate(
                "Today",
                listOf(
                    CallDetail(
                        0,
                        60000,
                        OUTGOING,
                        "February 19, 10:00",
                        User(2, "Jane", "https://placekitten.com/200/100")
                    ),
                    CallDetail(
                        0,
                        0,
                        INCOMING_MISSED,
                        "February 19, 10:00",
                        User(1, "John", "https://placekitten.com/200/300")
                    ),
                    CallDetail(
                        0,
                        0,
                        OUTGOING_MISSED,
                        "February 19, 10:00",
                        User(2, "Jane", "https://placekitten.com/200/100")
                    )
                )
            )
        )
    )
}
