package com.ignitetech.compose.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.*
import com.ignitetech.compose.R
import com.ignitetech.compose.call.CallScreen
import com.ignitetech.compose.chat.ChatScreen
import com.ignitetech.compose.chat.ChatsScreen
import com.ignitetech.compose.groups.GroupScreen
import com.ignitetech.compose.ui.Arguments
import com.ignitetech.compose.ui.Routes
import com.ignitetech.compose.utility.Content
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable(route = Routes.Home) {
            HomeScreen(navController = navController)
        }
        composable(route = Routes.Chats,
            arguments = listOf(
                navArgument(Arguments.UserId) {
                    type = NavType.IntType
                }
            )
        ) {
            ChatScreen(navController, it.arguments!!.getInt(Arguments.UserId))
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val tabs by viewModel.tabs.collectAsState()

    HomeScreen(navController, tabs)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    tabs: List<HomeTabs>
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { AppBar() },
        floatingActionButton = { FloatingButton(scaffoldState, scope) }
    ) { padding ->
        val pagerState = rememberPagerState(0)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Tabs(scope, pagerState, tabs)
            TabContents(navController, pagerState, tabs)
        }
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = stringResource(id = R.string.cd_search_conversation)
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_archive_24),
                    contentDescription = stringResource(id = R.string.cd_archive_chat)
                )
            }
            Box {
                val dismissDropDown = { showDropDown = false }
                IconButton(onClick = { showDropDown = !showDropDown }) {
                    Icon(Icons.Default.MoreVert, stringResource(id = R.string.cd_more_items))
                }
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = dismissDropDown
                ) {
                    DropdownMenuItem(onClick = {
                        dismissDropDown()
                        /*TODO*/
                    }) {
                        Text(text = stringResource(id = R.string.new_group))
                    }
                    DropdownMenuItem(onClick = {
                        dismissDropDown()
                        /*TODO*/
                    }) {
                        Text(text = stringResource(id = R.string.settings))
                    }
                }
            }
        })
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
private fun TabContents(
    navController: NavController,
    pagerState: PagerState,
    tabs: List<HomeTabs>
) {
    HorizontalPager(
        count = tabs.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!LocalInspectionMode.current) {
            when (val tab = tabs[it]) {
                HomeTabs.Chat -> ChatsScreen(navController)
                HomeTabs.Group -> GroupScreen(tab)
                else -> CallScreen()
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController(), listOf(HomeTabs.Chat, HomeTabs.Group, HomeTabs.Call))
}