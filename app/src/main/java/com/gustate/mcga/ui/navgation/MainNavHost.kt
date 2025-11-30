package com.gustate.mcga.ui.navgation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gustate.mcga.main.ui.MainPage
import com.gustate.mcga.panel.AodPanel
import com.gustate.mcga.panel.HomePanel
import com.gustate.mcga.panel.SearchPanel
import com.gustate.mcga.panel.SystemUIPanel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Destination,
    sharedTransitionScope: SharedTransitionScope
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        composable(route = Destination.MAIN.route) {
            MainPage(
                navController = navController,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = this
            )
        }
        composable("detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: "unknown"
            when (itemId) {
                "systemui" -> {
                    SystemUIPanel(
                        onBack = {
                            navController.popBackStack()
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this
                    )
                }

                "home" -> {
                    HomePanel(
                        onBack = {
                            navController.popBackStack()
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this
                    )
                }

                "aod" -> {
                    AodPanel(
                        onBack = {
                            navController.popBackStack()
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this
                    )
                }

                "search" -> {
                    SearchPanel(
                        onBack = {
                            navController.popBackStack()
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this
                    )
                }
            }
        }
    }
}
