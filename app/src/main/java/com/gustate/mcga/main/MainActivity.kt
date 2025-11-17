package com.gustate.mcga.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.navigation.compose.rememberNavController
import com.gustate.mcga.ui.navgation.Destination
import com.gustate.mcga.ui.navgation.MainNavHost
import com.gustate.mcga.ui.theme.MakeColorGreatAgainTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MakeColorGreatAgainTheme {
                val navController = rememberNavController()
                SharedTransitionLayout {
                    MainNavHost(
                        navController = navController,
                        startDestination = Destination.MAIN,
                        sharedTransitionScope = this
                    )
                }
            }
        }
    }
}