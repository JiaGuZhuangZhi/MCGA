package com.gustate.mcga.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true
            )
    ) {
        Card(

        ) {

        }
    }
}