package com.gustate.mcga.ui.navgation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gustate.mcga.data.setting.aod.AodPage
import com.gustate.mcga.data.setting.home.HomePage
import com.gustate.mcga.data.setting.search.SearchPage
import com.gustate.mcga.data.setting.systemui.SystemuiPage
import com.gustate.mcga.data.setting.systemui.tile.TilePage
import com.gustate.mcga.data.setting.wallet.WalletPage
import com.gustate.mcga.data.viewmodel.ModuleViewModel
import com.gustate.mcga.main.ui.MainPage
import com.gustate.mcga.panel.PanelRenderer
import com.gustate.mcga.ui.theme.gustateColors
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavHost(
    moduleViewModel: ModuleViewModel,
    navController: NavHostController,
    startDestination: Destination,
    sharedTransitionScope: SharedTransitionScope
) {
    NavHost(
        modifier = Modifier.background(color = gustateColors.background),
        navController = navController,
        startDestination = startDestination.route
    ) {

        composable(route = Destination.MAIN.route) {
            MainPage(
                viewModel = moduleViewModel,
                navController = navController,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = this
            )
        }

        allPages.forEach { page ->
            composable(route = page.route) {
                page.PanelRenderer(
                    hazeState = rememberHazeState(),
                    navController = navController,
                    isReturnGroup = false
                )
            }
        }

    }
}

val allPages = listOf(
    HomePage,
    WalletPage,
    SearchPage,
    SystemuiPage,
    TilePage,
    AodPage
)