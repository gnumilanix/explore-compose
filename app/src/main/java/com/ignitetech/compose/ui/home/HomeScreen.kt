package com.ignitetech.compose.ui.home

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import com.ignitetech.compose.R
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.ui.Screens.HomeScreens
import com.ignitetech.compose.ui.call.CallScreen
import com.ignitetech.compose.ui.chat.ChatsScreen
import com.ignitetech.compose.ui.groups.GroupScreen
import com.ignitetech.compose.ui.theme.ComposeTheme
import com.ignitetech.compose.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val FAB_TOAST_MESSAGE = "Launch new chat"

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(navController, state.tabs)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    tabs: List<HomeScreens>,
    isSearchActive: Boolean = false
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var onSearchMode by remember {
        mutableStateOf(isSearchActive)
    }

    BackHandler(onSearchMode) {
        onSearchMode = false
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .semantics { screen = Screens.Home }) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { AppBar(navController, onSearch = { onSearchMode = it }) },
            floatingActionButton = {
                if (!onSearchMode) {
                    FloatingButton(scaffoldState, scope)
                }
            },
            modifier = Modifier.semantics { screen = Screens.Home }
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
        AnimatedVisibility(
            visible = onSearchMode,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SearchAppBar(onSearch = { onSearchMode = it })
                SearchResults()
            }
        }
    }
}

@Composable
private fun SearchResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeTheme.colors.materialColors.background)
            .clickable {}
    ) {

    }
}

@Composable
private fun AppBar(
    navController: NavController,
    onSearch: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        DefaultAppBar(navController, onSearch)
    }
}

@Composable
private fun DefaultAppBar(
    navController: NavController,
    onSearch: (Boolean) -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            var showDropDown by remember {
                mutableStateOf(false)
            }

            IconButton(onClick = { onSearch(true) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = stringResource(id = R.string.cd_search_conversation),
                    modifier = Modifier.semantics { drawableId = R.drawable.baseline_search_24 }
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_archive_24),
                    contentDescription = stringResource(id = R.string.cd_archive_chat),
                    modifier = Modifier.semantics {
                        drawableId = R.drawable.baseline_archive_24
                    }
                )
            }
            Box {
                val dismissDropDown = { showDropDown = false }
                IconButton(onClick = { showDropDown = !showDropDown }) {
                    Icon(
                        Icons.Default.MoreVert,
                        stringResource(id = R.string.cd_more_items),
                        modifier = Modifier.semantics {
                            drawableVector = Icons.Default.MoreVert
                        }
                    )
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
                        navController.navigate(Screens.Settings.route)
                    }) {
                        Text(text = stringResource(id = R.string.settings))
                    }
                }
            }
        },
        modifier = Modifier.statusBarsPadding()
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun SearchAppBar(onSearch: (Boolean) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
        Surface(
            color = ComposeTheme.colors.appBar,
            contentColor = ComposeTheme.colors.appBarContent
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    IconButton(onClick = { onSearch(false) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            stringResource(id = R.string.cd_back),
                            modifier = Modifier.semantics {
                                drawableVector = Icons.Default.MoreVert
                            }
                        )
                    }

                    var query by remember {
                        mutableStateOf("")
                    }

                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                stringResource(R.string.ph_search),
                                style = MaterialTheme.typography.body1,
                                color = ComposeTheme.colors.contextualAppBarContent,
                                maxLines = 1,
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .fillMaxSize()
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1.0f)
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    keyboardController?.show()
                                }
                            }
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 8.dp)
                ) {
                    FilterChip(R.string.photo, R.drawable.baseline_image_24)
                    Spacer(modifier = Modifier.size(8.dp))
                    FilterChip(R.string.video, R.drawable.baseline_video_file_24)
                    Spacer(modifier = Modifier.size(8.dp))
                    FilterChip(R.string.document, R.drawable.baseline_file_24)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun FilterChip(@StringRes title: Int, @DrawableRes icon: Int) {
    Chip(onClick = {/*TODO*/ }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = title),
            )
        }
    }
}

@Composable
fun FloatingButton(scaffoldState: ScaffoldState, scope: CoroutineScope) {
    FloatingActionButton(onClick = {
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(FAB_TOAST_MESSAGE)
        }
    }, backgroundColor = MaterialTheme.colors.secondary) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_chat_24),
            contentDescription = stringResource(id = R.string.cd_new_chat),
            modifier = Modifier
                .padding(8.dp)
                .semantics { drawableId = R.drawable.baseline_chat_24 },
            tint = Color.White
        )
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun Tabs(
    scope: CoroutineScope,
    pagerState: PagerState,
    tabs: List<HomeScreens>
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = { Spacer(modifier = Modifier.height(4.dp)) },
        indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(
                    pagerState,
                    it
                ),
                height = 4.dp,
                color = Color.White
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
    tabs: List<HomeScreens>
) {
    HorizontalPager(
        count = tabs.size,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!LocalInspectionMode.current) {
            when (tabs[it]) {
                HomeScreens.Chats -> ChatsScreen(navController, viewModel = hiltViewModel())
                HomeScreens.Groups -> GroupScreen(viewModel = hiltViewModel())
                else -> CallScreen(viewModel = hiltViewModel())
            }
        }
    }
}

@Preview
@Composable
@ExcludeFromGeneratedCoverageReport
fun HomeScreenPreview() {
    ComposeTheme {
        HomeScreen(
            rememberNavController(),
            listOf(HomeScreens.Chats, HomeScreens.Groups, HomeScreens.Calls)
        )
    }
}

@Preview
@Composable
@ExcludeFromGeneratedCoverageReport
fun HomeScreenSearchPreview() {
    ComposeTheme {
        HomeScreen(
            rememberNavController(),
            listOf(HomeScreens.Chats, HomeScreens.Groups, HomeScreens.Calls),
            true
        )
    }
}

@Preview
@Composable
@ExcludeFromGeneratedCoverageReport
fun HomeScreenSearchBarPreview() {
    ComposeTheme {
        SearchAppBar {}
    }
}
