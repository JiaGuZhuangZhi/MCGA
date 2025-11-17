package com.gustate.mcga.main.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.MainViewModel
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

    val viewModel = viewModel<MainViewModel>()
    val isModuleActive = viewModel.isModuleActive.value ?: false
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 1) { Destination.entries.size }

    Scaffold(
        topBar = {
            AppTopBar(
                title =
                    {
                        Text(
                            text = when (pagerState.currentPage) {
                                Destination.SETTING.ordinal ->
                                    stringResource(R.string.setting)
                                Destination.HOME.ordinal ->
                                    stringResource(R.string.app_name)
                                Destination.ABOUT.ordinal ->
                                    stringResource(R.string.about)
                                else -> stringResource(R.string.app_name)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                scrollBehavior = scrollBehavior,
                isHomePage = pagerState.currentPage == Destination.HOME.ordinal
            )
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    Destination.entries.forEachIndexed { index, destination ->
                        if (destination == Destination.SETTING && !isModuleActive)
                            return@forEachIndexed
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
                                    painter = painterResource(id =
                                        if (isTabSelected) destination.focusIcon
                                        else destination.icon
                                    ),
                                    contentDescription =
                                        stringResource(id = destination.contentDescription)
                                )
                            },
                            label = {
                                Text(text = stringResource(id = destination.label))
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
            when (Destination.entries[page]) {
                Destination.SETTING -> SettingPage(
                    modifier = pagerModifier
                        .fillMaxSize(),
                    navController = navController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
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