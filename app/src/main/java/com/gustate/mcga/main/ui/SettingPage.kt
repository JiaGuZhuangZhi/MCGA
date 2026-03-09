package com.gustate.mcga.main.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gustate.mcga.R
import com.gustate.mcga.ui.widget.OptionWidget
import com.gustate.mcga.ui.widget.SplicedColumnGroup

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SettingPage(
    modifier: Modifier,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            )
    ) {
        SplicedColumnGroup(
            modifier = Modifier,
            title = stringResource(R.string.main),
            content = listOf(
                { shape ->
                    with(receiver = sharedTransitionScope) {
                        OptionWidget(
                            modifier = modifier
                                .clip(shape)
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(key = "systemui"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                                    clipInOverlayDuringTransition = OverlayClip(clipShape = shape)
                                ),
                            painter = painterResource(id = R.drawable.ic_system_ui),
                            title = stringResource(id = R.string.systemui),
                            onClick = {
                                navController.navigate(route = "detail/systemui")
                            }
                        )
                    }
                },
                { shape ->
                    with(receiver = sharedTransitionScope) {
                        OptionWidget(
                            modifier = modifier
                                .clip(shape)
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(key = "home"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                                    clipInOverlayDuringTransition = OverlayClip(clipShape = shape)
                                ),
                            painter = painterResource(id = R.drawable.ic_home),
                            title = stringResource(id = R.string.hook_launcher),
                            onClick = {
                                navController.navigate(route = "detail/home")
                            }
                        )
                    }
                }, { shape ->
                    with(receiver = sharedTransitionScope) {
                        OptionWidget(
                            modifier = modifier
                                .clip(shape)
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(key = "aod"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                                    clipInOverlayDuringTransition = OverlayClip(clipShape = shape)
                                ),
                            painter = painterResource(id = R.drawable.aod),
                            title = stringResource(id = R.string.aod),
                            onClick = {
                                navController.navigate(route = "detail/aod")
                            }
                        )
                    }
                }
            )
        )
        SplicedColumnGroup(
            modifier = Modifier,
            title = stringResource(R.string.more),
            content = listOf(
                { shape ->
                    with(receiver = sharedTransitionScope) {
                        OptionWidget(
                            modifier = modifier
                                .clip(shape)
                                .sharedBounds(
                                    sharedContentState =
                                        rememberSharedContentState(key = "search"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    enter = fadeIn(),
                                    exit = fadeOut(),
                                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                                    clipInOverlayDuringTransition = OverlayClip(clipShape = shape)
                                ),
                            painter = painterResource(id = R.drawable.ic_search),
                            title = stringResource(id = R.string.hook_search),
                            onClick = {
                                navController.navigate("detail/search")
                            }
                        )
                    }
                }
            )
        )
    }
}