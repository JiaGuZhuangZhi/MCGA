package com.gustate.mcga.main.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gustate.mcga.data.viewmodel.ModuleViewModel
import com.gustate.mcga.main.navgation.Destination
import com.gustate.mcga.ui.widget.AppTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainPage(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val viewModel = viewModel<ModuleViewModel>()
    val moduleUiState by viewModel.uiState
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState())
    val scope = rememberCoroutineScope()

    val availableDestinations = remember(
        key1 = moduleUiState.isModuleActive
    ) {
        Destination.entries.filter {
            // 如果是设置页且模块未激活 过滤掉
            !(it == Destination.SETTING && !moduleUiState.isModuleActive)
        }
    }
    val pagerState = rememberPagerState(
        initialPage = availableDestinations
            .indexOf(element = Destination.HOME)
    ) {
        availableDestinations.size
    }
    val currentDestination = remember(
        key1 = pagerState.currentPage,
        key2 = moduleUiState.isModuleActive
    ) {
        availableDestinations
            .getOrNull(index = pagerState.currentPage)
            ?: Destination.HOME
    }
    LaunchedEffect(
        key1 = moduleUiState.isModuleActive
    ) {
        if (moduleUiState.isModuleActive) {
            pagerState.animateScrollToPage(
                page = availableDestinations.indexOf(Destination.HOME)
            )
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = stringResource(id = currentDestination.label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior,
                isHomePage = currentDestination == Destination.HOME
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    availableDestinations.forEachIndexed { index, destination ->
                        val isTabSelected = pagerState.currentPage == index
                        NavigationBarItem(
                            selected = isTabSelected,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(page = index)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id =
                                            if (isTabSelected) destination.focusIcon
                                            else destination.icon
                                    ),
                                    contentDescription =
                                        stringResource(id = destination.contentDescription)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = destination.navLabel)
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(paddingValues)
        ) { page ->
            val pagerModifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
            when (availableDestinations[page]) {
                Destination.SETTING -> {
                    SettingPage(
                        modifier = pagerModifier
                            .fillMaxSize(),
                        navController = navController,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }

                Destination.HOME -> HomePage(
                    modifier = pagerModifier
                        .fillMaxSize(),
                    viewModel = viewModel
                )

                Destination.ABOUT -> AboutPage(
                    modifier = pagerModifier
                        .fillMaxSize()
                )
            }
        }
    }
}
