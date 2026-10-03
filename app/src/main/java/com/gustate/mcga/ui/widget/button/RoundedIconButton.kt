package com.gustate.mcga.ui.widget.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.gustateColors
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun RoundedIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    radius: Dp = 16.dp,
    painter: Painter,
    tint: Color = gustateColors.onFilledTonalButton,
    onClick: () -> Unit,
) {
    RoundedIconButton(
        modifier = modifier,
        size = size,
        radius = radius,
        painter = painter,
        imageVector = null,
        tint = tint,
        onClick = onClick
    )
}

@Composable
fun RoundedIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    radius: Dp = 16.dp,
    imageVector: ImageVector,
    tint: Color = gustateColors.onFilledTonalButton,
    onClick: () -> Unit,
) {
    RoundedIconButton(
        modifier = modifier,
        size = size,
        radius = radius,
        painter = null,
        imageVector = imageVector,
        tint = tint,
        onClick = onClick
    )
}

@Composable
private fun RoundedIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    radius: Dp = 16.dp,
    painter: Painter? = null,
    imageVector: ImageVector? = null,
    tint: Color = gustateColors.onFilledTonalButton,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                shape = ContinuousRoundedRectangle(size = radius),
                color = gustateColors.container
            )
            .clip(shape = ContinuousRoundedRectangle(size = radius))
            .clickable { onClick() }
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        if (imageVector != null) {
            Icon(
                modifier = Modifier
                    .size(size * 0.6f)
                    .alpha(alpha = 0.3f),
                imageVector = imageVector,
                contentDescription = null,
                tint = tint
            )
        } else if (painter != null) {
            Icon(
                modifier = Modifier
                    .size(size * 0.6f)
                    .alpha(0.3f),
                painter = painter,
                contentDescription = null,
                tint = tint
            )
        }
    }
}