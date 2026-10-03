package com.gustate.mcga.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.gustateColors

@Composable
fun RoundedCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    uncheckedColor: Color = gustateColors.filledTonalButton,
    checkedColor: Color = gustateColors.filledButton,
    checkmarkColor: Color = gustateColors.onFilledButton
) {
    Box(
        modifier = modifier
            .size(size)
            .padding(size * 0.12f)
            .clip(CircleShape)
            .background(if (checked) checkedColor else uncheckedColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.check),
            contentDescription = null,
            tint = checkmarkColor,
            modifier = Modifier.size(size * 0.64f)
        )
    }
}