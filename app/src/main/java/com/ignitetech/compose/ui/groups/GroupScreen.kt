package com.ignitetech.compose.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignitetech.compose.R
import com.ignitetech.compose.data.group.Group
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.ui.theme.Grey200

@Composable
fun GroupScreen(viewModel: GroupsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    GroupScreen(state.groups)
}

@Composable
fun GroupScreen(groups: List<Group>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(groups) {
            Group(it)
        }
    }
}

@Composable
fun Group(group: Group) {
    Card(
        modifier = Modifier.clickable { },
        elevation = 10.dp
    ) {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1.0f)
            ) {
                items(group.users) {
                    User(it)
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Grey200)
            )
            Text(
                text = group.name,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 4.dp, 8.dp, 4.dp)
            )
        }
    }
}

@Composable
fun User(user: User) {
    AsyncImage(
        model = user.avatar,
        placeholder = painterResource(id = R.drawable.baseline_person_24),
        error = painterResource(id = R.drawable.baseline_person_24),
        fallback = painterResource(id = R.drawable.baseline_person_24),
        contentDescription = stringResource(R.string.cd_user_profile),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.0f)
            .clip(CircleShape)
            .border(1.dp, Color(0xff76d275), CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
fun GroupScreenPreview() {
    val users = listOf(
        User(1, "John", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300")
    )
    val moreUsers = listOf(
        User(1, "John", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300"),
        User(1, "Jane", "https://placekitten.com/200/300")
    )
    val lotsOfUsers = listOf(
        User(1, "John", "https://placekitten.com/200/300"),
        User(1, "Jack", "https://placekitten.com/200/300"),
        User(1, "Jane", "https://placekitten.com/200/300"),
        User(1, "Amy", "https://placekitten.com/200/300"),
        User(1, "Cindy", "https://placekitten.com/200/300"),
        User(1, "Mandy", "https://placekitten.com/200/300")
    )
    val groups = listOf(
        Group("Friends", moreUsers),
        Group("Family", users),
        Group("Friends", moreUsers),
        Group("Family", users),
        Group("Friends", moreUsers),
        Group("Family", lotsOfUsers),
    )
    GroupScreen(groups)
}