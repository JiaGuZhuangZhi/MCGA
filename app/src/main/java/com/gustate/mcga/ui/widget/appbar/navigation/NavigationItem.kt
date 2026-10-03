package com.gustate.mcga.ui.widget.appbar.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.gustate.mcga.ui.theme.ThemeType
import com.gustate.mcga.ui.theme.gustateThemes
import com.gustate.mcga.ui.widget.appbar.navigation.ios.IosNavigationItem
import com.gustate.mcga.ui.widget.appbar.navigation.material.MaterialNavigationItem
import com.gustate.mcga.ui.widget.appbar.navigation.uotan.GustateNavigationBarItem

@Composable
fun RowScope.NavigationItem(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: Painter,
    contentDestination: String,
    label: String,
    onClick: () -> Unit
) {
    when (gustateThemes.themeType) {
        ThemeType.Gustate -> {
            GustateNavigationBarItem(
                modifier = modifier,
                selected = selected,
                icon = icon,
                contentDestination = contentDestination,
                label = label,
                onClick = onClick
            )
        }

        ThemeType.Ios -> {
            IosNavigationItem(
                modifier = modifier,
                icon = icon,
                label = label,
                onClick = onClick
            )
        }

        ThemeType.Material -> {
            MaterialNavigationItem(
                modifier = modifier,
                selected = selected,
                icon = icon,
                contentDestination = contentDestination,
                label = label,
                onClick = onClick
            )
        }
    }
}