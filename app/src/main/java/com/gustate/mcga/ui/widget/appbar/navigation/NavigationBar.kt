package com.gustate.mcga.ui.widget.appbar.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.ThemeType
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.gustateThemes
import com.gustate.mcga.ui.widget.appbar.navigation.ios.IosNavigationBar
import com.gustate.mcga.ui.widget.appbar.navigation.uotan.GustateNavigationBar
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    tabsCount: Int = 0,
    selectedTabIndex: () -> Int = { 0 },
    onTabSelected: (Int) -> Unit = {},
    backdrop: Backdrop = rememberLayerBackdrop(),
    hazeState: HazeState = rememberHazeState(),
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable RowScope.() -> Unit
) {
    when (gustateThemes.themeType) {
        ThemeType.Gustate -> {
            GustateNavigationBar(
                modifier = modifier,
                hazeState = hazeState,
                paddingValues = paddingValues,
                content = content
            )
        }

        ThemeType.Ios -> {
            IosNavigationBar(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected,
                backdrop = backdrop,
                tabsCount = tabsCount,
                modifier = modifier
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                    ),
                content = content
            )
        }

        ThemeType.Material -> {
            ShortNavigationBar(
                modifier = modifier
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp),
                containerColor = gustateColors.container,
                windowInsets = NavigationBarDefaults.windowInsets,
                content = {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        content()
                    }
                }
            )
        }
    }
}