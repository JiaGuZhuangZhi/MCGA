package com.gustate.mcga.ui.page

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.gustate.mcga.ui.theme.baseHazeStyle
import com.gustate.mcga.ui.widget.AppTopBar
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BasePanelPage(
    title: String,
    onBackClick: () -> Unit,
    onRestartClick: () -> Unit,
    sharedKey: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    content: @Composable (
        paddingValues: PaddingValues,
        scrollBehavior: TopAppBarScrollBehavior,
        hazeState: HazeState
    ) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState())
    val hazeState = rememberHazeState()
    with(receiver = sharedTransitionScope) {
        Scaffold(
            topBar = {
                AppTopBar(
                    modifier = Modifier
                        .hazeEffect(
                            state = hazeState,
                            style = baseHazeStyle()
                        ) {
                            progressive = HazeProgressive
                                .verticalGradient(startIntensity = 1f, endIntensity = 0f)
                        },
                    title = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehavior,
                    isBackAvailable = true,
                    onBackClick = onBackClick,
                    isRestartAvailable = true,
                    onRestartClick = onRestartClick
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = sharedKey),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
        ) { paddingValues ->
            content(paddingValues, scrollBehavior, hazeState)
        }
    }
}