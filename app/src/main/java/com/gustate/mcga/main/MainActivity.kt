package com.gustate.mcga.main

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.gustate.mcga.data.viewmodel.ModuleViewModel
import com.gustate.mcga.ui.navgation.Destination
import com.gustate.mcga.ui.navgation.MainNavHost
import com.gustate.mcga.ui.theme.MakeColorGreatAgainTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val viewModel: ModuleViewModel by viewModels()
        splashScreen.setKeepOnScreenCondition { /*!viewModel.uiState.value.isReady*/false }
        enableEdgeToEdge()
        setContent {
            MakeColorGreatAgainTheme {
                val navController = rememberNavController()
                SharedTransitionLayout {
                    MainNavHost(
                        moduleViewModel = viewModel,
                        navController = navController,
                        startDestination = Destination.MAIN,
                        sharedTransitionScope = this
                    )
                }
            }
        }
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.uiState.value.isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            }
        )
    }

}