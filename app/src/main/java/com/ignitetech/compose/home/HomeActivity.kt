package com.ignitetech.compose.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.*
import com.ignitetech.compose.R
import com.ignitetech.compose.conversation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Content {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun FloatingButton(scaffoldState: ScaffoldState, scope: CoroutineScope) {
    FloatingActionButton(onClick = {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar("Launch attach")
        }
    }, backgroundColor = MaterialTheme.colors.secondary) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_chat_24),
            contentDescription = stringResource(id = R.string.cd_new_chat),
            modifier = Modifier.padding(8.dp),
            tint = Color.White
        )
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun Tabs(
    scope: CoroutineScope,
    pagerState: PagerState,
    tabs: List<HomeTabs>
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = { Spacer(modifier = Modifier.height(4.dp)) },
        indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(
                    pagerState,
                    it
                ), height = 4.dp, color = Color.White
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabs.forEachIndexed { index, tab ->
            LeadingIconTab(selected = index == pagerState.currentPage, onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }, icon = {
                Icon(
                    painter = painterResource(id = tab.icon),
                    contentDescription = stringResource(id = tab.name)
                )
            }, text = {
                Text(text = stringResource(id = tab.name))
            })
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun TabContents(pagerState: PagerState, tabs: List<HomeTabs>) {
    HorizontalPager(
        count = tabs.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) {
        when (val tab = tabs[it]) {
            HomeTabs.Chat -> ChatScreen(tab)
            HomeTabs.Group -> GroupScreen(tab)
            else -> CallScreen(tab)
        }
    }
}

@Composable
private fun ChatScreen(tab: HomeTabs) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFF88FFFF))
    ) {
        Text(text = stringResource(id = tab.name))
    }
}

@Composable
private fun GroupScreen(tab: HomeTabs) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFE1FFB1))
    ) {
        Text(text = stringResource(id = tab.name))
    }
}

@Composable
private fun CallScreen(tab: HomeTabs) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFFFFFA8))
    ) {
        Text(text = stringResource(id = tab.name))
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(HomeTabs.Chat)
}