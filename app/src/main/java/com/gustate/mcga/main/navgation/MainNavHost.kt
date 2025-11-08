package com.gustate.mcga.main.navgation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gustate.mcga.data.viewmodel.HomeViewModel
import com.gustate.mcga.data.viewmodel.SearchViewModel
import com.gustate.mcga.main.ui.AboutPage
import com.gustate.mcga.main.ui.HomePage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.HOME -> HomePage(modifier, scrollBehavior, homeViewModel, searchViewModel)
                    Destination.ABOUT -> AboutPage()
                }
            }
        }
    }
}
