package com.gustate.mcga.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.gustate.mcga.data.setting.MainPage
import com.gustate.mcga.panel.PanelRenderer
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun SettingPage(
    modifier: Modifier,
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            )
            .padding(paddingValues)
    ) {
        MainPage.PanelRenderer(
            hazeState = rememberHazeState(),
            navController = navController,
            isReturnGroup = true
        )
    }
}