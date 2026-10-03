package com.gustate.mcga.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun basicButtonColors() = ButtonColors(
    containerColor = gustateColors.container.copy(alpha = 0.6f),
    contentColor = gustateColors.onContainerPrimary,
    disabledContainerColor = gustateColors.container.copy(alpha = 0.2f),
    disabledContentColor = gustateColors.onContainerPrimary.copy(alpha = 0.2f)
)

@Composable
fun filledButtonColors() = ButtonColors(
    containerColor = gustateColors.filledButton,
    contentColor = gustateColors.onFilledButton,
    disabledContainerColor = gustateColors.filledButton.copy(alpha = 0.4f),
    disabledContentColor = gustateColors.onFilledButton.copy(alpha = 0.4f)
)

@Composable
fun filledTonalButtonColors() = ButtonColors(
    containerColor = gustateColors.filledTonalButton,
    contentColor = gustateColors.onFilledTonalButton,
    disabledContainerColor = gustateColors.filledTonalButton.copy(alpha = 0.4f),
    disabledContentColor = gustateColors.onFilledTonalButton.copy(alpha = 0.4f)
)

@Composable
fun textButtonColors() = ButtonColors(
    containerColor = Color.Transparent,
    contentColor = gustateColors.onFilledTonalButton,
    disabledContainerColor = Color.Transparent,
    disabledContentColor = gustateColors.onFilledTonalButton.copy(alpha = 0.4f)
)