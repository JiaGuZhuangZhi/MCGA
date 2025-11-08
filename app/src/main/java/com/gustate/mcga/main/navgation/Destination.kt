package com.gustate.mcga.main.navgation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.gustate.mcga.R

enum class Destination(
    val route: String,
    @param:StringRes @field:StringRes
    val label: Int,
    val icon: ImageVector,
    val focusIcon: ImageVector,
    @param:StringRes @field:StringRes
    val contentDescription: Int
) {
    HOME(
        route = "home", label = R.string.home,
        icon = Icons.Outlined.Home, focusIcon = Icons.Filled.Home,
        contentDescription = R.string.home
    ),
    ABOUT(
        route = "about", label = R.string.about,
        icon = Icons.Outlined.Info, focusIcon = Icons.Filled.Info,
        contentDescription = R.string.home
    )
}
