package com.gustate.mcga.ui.navgation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.gustate.mcga.data.viewmodel.ModuleViewModel
import com.gustate.mcga.data.viewmodel.SystemUIViewModel
import com.gustate.mcga.main.ui.MainPage
import com.gustate.mcga.panel.AodPanel
import com.gustate.mcga.panel.HomePanel
import com.gustate.mcga.panel.SearchPanel
import com.gustate.mcga.panel.SystemUIPanel
import com.gustate.mcga.panel.WalletPanel
import com.gustate.mcga.panel.systemui.TilePanel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavHost(
    moduleViewModel: ModuleViewModel,
    navController: NavHostController,
    startDestination: Destination,
    sharedTransitionScope: SharedTransitionScope
) {
    NavHost(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
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

        // 将 SystemUI 相关的所有页面打包进一个嵌套图
        // 这样只要在这个组内，ViewModel 就会一直存活
        navigation(
            startDestination = Destination.SYSTEMUI.route,
            route = Destination.SYSTEMUI_HOST.route
        ) {
            // 中间层： SystemUIPanel
            composable(route = Destination.SYSTEMUI.route) { backStackEntry ->
                // 获取当前嵌套图作用域下的 ViewModel
                val sharedVm = backStackEntry.sharedViewModel<SystemUIViewModel>(navController)
                SystemUIPanel(
                    onBack = { navController.popBackStack() },
                    viewModel = sharedVm,
                    navController = navController,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this
                )
            }
            // 具体 Panel 层：使用参数匹配
            composable(route = "systemui/{itemId}") { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: "unknown"
                // 关键点：这里获取的是和上面同一个实例
                val sharedVm: SystemUIViewModel = backStackEntry.sharedViewModel(navController)

                when (itemId) {
                    "tile" -> {
                        TilePanel(
                            onBack = { navController.popBackStack() },
                            viewModel = sharedVm,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = this
                        )
                    }

                    else -> { /* 错误处理 */
                    }
                }
            }
        }

        composable(route = "detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: "unknown"
            when (itemId) {
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

                "wallet" -> {
                    WalletPanel(
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

// 获取 Graph 作用域的 ViewModel
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(key1 = this) {
        navController.getBackStackEntry(route = navGraphRoute)
    }
    return viewModel(viewModelStoreOwner = parentEntry)
}