package com.ignitetech.compose.ui.chat

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.theme.ComposeTheme
import com.ignitetech.compose.ui.theme.Purple500
import com.ignitetech.compose.utility.ExcludeFromGeneratedCoverageReport
import com.ignitetech.compose.utility.drawableVector
import com.ignitetech.compose.utility.isActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface Selector

sealed class EditorState {

    object None : EditorState()
    object Typing : EditorState()
    object Emoji : EditorState(), Selector
    object Attachment : EditorState(), Selector
}

@Composable
internal fun Editor(
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
            .size(40.dp)
            .semantics { drawableVector = icon },
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(Purple500)
    ) {
        Icon(
            icon,
            contentDescription,
            modifier = Modifier.fillMaxSize(),
            tint = Color.White
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
fun EditorPreview() {
    ComposeTheme {
        Editor()
    }
}
