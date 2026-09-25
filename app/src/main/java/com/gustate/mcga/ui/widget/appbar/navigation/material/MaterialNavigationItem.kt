package com.gustate.mcga.ui.widget.appbar.navigation.material

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.gustate.mcga.ui.theme.gustateColors

@Composable
fun MaterialNavigationItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: Painter,
    contentDestination: String,
    label: String,
    onClick: () -> Unit
) {
    ShortNavigationBarItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        colors = NavigationItemColors(
            selectedIconColor = gustateColors.onFilledTonalButton,
            selectedTextColor = gustateColors.onBackgroundPrimary,
            selectedIndicatorColor = gustateColors.filledTonalButton,
            unselectedIconColor = gustateColors.onBackgroundSecondary,
            unselectedTextColor = gustateColors.onBackgroundSecondary,
            disabledIconColor = gustateColors.onBackgroundSecondary,
            disabledTextColor = gustateColors.onBackgroundSecondary
        ),
        icon = {
            Icon(
                painter = icon,
                contentDescription = contentDestination
            )
        },
        label = {
            Text(
                text = label
            )
        }
    )
}