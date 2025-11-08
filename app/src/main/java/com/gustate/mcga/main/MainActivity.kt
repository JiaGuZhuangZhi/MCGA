package com.gustate.mcga.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.data.viewmodel.HomeViewModel
import com.gustate.mcga.data.viewmodel.SearchViewModel
import com.gustate.mcga.main.ui.MainContent
import com.gustate.mcga.ui.theme.MakeColorGreatAgainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeViewModel = viewModel<HomeViewModel>()
            val searchViewModel = viewModel<SearchViewModel>()
            MakeColorGreatAgainTheme {
                MainContent(homeViewModel, searchViewModel)
            }
        }
    }
}