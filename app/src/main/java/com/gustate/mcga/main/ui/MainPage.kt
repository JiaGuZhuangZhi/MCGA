package com.gustate.mcga.main.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.ModuleViewModel
import com.gustate.mcga.main.navgation.Destination
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.gustateThemes
import com.gustate.mcga.ui.widget.appbar.TopAppBar
import com.gustate.mcga.ui.widget.appbar.navigation.NavigationBar
import com.gustate.mcga.ui.widget.appbar.navigation.NavigationItem
import com.gustate.mcga.ui.widget.appbar.rememberTopBarState
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun MainPage(
    viewModel: ModuleViewModel,
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val moduleUiState by viewModel.uiState.collectAsState()
    /*val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState())**/
    val topBarState = rememberTopBarState()
    val scope = rememberCoroutineScope()

    val availableDestinations = remember(key1 = moduleUiState.isModuleActive) {
        Destination.entries.filter {
            !(it == Destination.SETTING && !moduleUiState.isModuleActive)
        }
    }
    val pagerState = rememberPagerState(initialPage = 0) {
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
    val hazeState = rememberHazeState()
    val backdrop = rememberLayerBackdrop()

    Scaffold(
        topBar = {
            TopAppBar(
                hazeState = hazeState,
                title = stringResource(id = currentDestination.label),
                topBarState = topBarState,
                leftIcon = {
                    val visible = remember(key1 = pagerState.currentPage) {
                        pagerState.currentPage == 0
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 180)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 180))
                    ) {
                        Icon(
                            painter =
                                if (gustateThemes.isDynamicColor)
                                    painterResource(id = R.drawable.ic_launcher_monochrome)
                                else painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = stringResource(id = R.string.app_name),
                            tint =
                                if (gustateThemes.isDynamicColor) gustateColors.primary
                                else Color.Unspecified
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                // modifier
                tabsCount = 3,
                selectedTabIndex = {
                    pagerState.currentPage
                },
                onTabSelected = { page ->
                    scope.launch {
                        pagerState.animateScrollToPage(page = page)
                    }
                },
                backdrop = backdrop,
                hazeState = hazeState
                //paddingValues = PaddingValues()
            ) {
                availableDestinations.forEachIndexed { page, destination ->
                    val isTabSelected = pagerState.currentPage == page
                    NavigationItem(
                        //modifier
                        selected = isTabSelected,
                        icon = painterResource(
                            id = if (isTabSelected) destination.focusIcon else destination.icon
                        ),
                        contentDestination =
                            stringResource(id = destination.contentDescription),
                        label = stringResource(id = destination.navLabel),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(page = page)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .hazeSource(hazeState)
                .layerBackdrop(backdrop)
        ) { page ->
            val pagerModifier = Modifier
                .background(color = gustateColors.background)
                .nestedScroll(topBarState.nestedScrollConnection)
            when (availableDestinations[page]) {
                Destination.HOME -> HomePage(
                    modifier = pagerModifier
                        .fillMaxSize(),
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )

                Destination.SETTING -> {
                    SettingPage(
                        modifier = pagerModifier
                            .fillMaxSize(),
                        navController = navController,
                        paddingValues = paddingValues
                    )
                }

                Destination.ABOUT -> AboutPage(
                    modifier = pagerModifier
                        .fillMaxSize(),
                    navController = navController,
                    paddingValues = paddingValues
                )
            }
        }
    }
}
