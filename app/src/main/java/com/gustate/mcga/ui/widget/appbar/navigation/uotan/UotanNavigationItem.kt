package com.gustate.mcga.ui.widget.appbar.navigation.uotan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustate.mcga.ui.theme.gustateColors
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun RowScope.GustateNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: Painter,
    contentDestination: String,
    label: String,
    onClick: () -> Unit
) {
    val itemTint =
        if (selected) gustateColors.onBackgroundPrimary
        else gustateColors.onBackgroundSecondary
    Box(
        modifier = modifier
            .padding(vertical = 6.dp)
            .clip(shape = ContinuousRoundedRectangle(size = 12.dp))
            .clickable(
                enabled = true,
                onClick = onClick
            )
            .weight(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(size = 24.dp),
                painter = icon,
                contentDescription = contentDestination,
                tint = itemTint
            )
            Text(
                text = label,
                modifier = Modifier
                    .padding(top = 2.dp),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = itemTint
            )
        }
    }
}