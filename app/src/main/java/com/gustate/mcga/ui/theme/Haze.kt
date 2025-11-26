package com.gustate.mcga.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint

@Composable
fun baseHazeStyle(
    blurRadius: Dp = 24.dp,
    containerColor: Color =
        MaterialTheme.colorScheme.background,
    lightAlpha: Float= 0.36f,
    darkAlpha: Float = 0.48f,
): HazeStyle = HazeStyle(
    blurRadius = blurRadius,
    backgroundColor = containerColor,
    tint = HazeTint(
        color = containerColor.copy(
            alpha =
                if (isSystemInDarkTheme()) lightAlpha
                else darkAlpha
        )
    ),
)