package com.gustate.mcga.main.navgation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gustate.mcga.R

enum class Destination(
    val route: String,
    @param:StringRes @field:StringRes
    val label: Int,
    @param:DrawableRes @field:DrawableRes
    val icon: Int,
    @param:DrawableRes @field:DrawableRes
    val focusIcon: Int,
    @param:StringRes @field:StringRes
    val contentDescription: Int
) {
    SETTING(
        route = "setting", label = R.string.setting,
        icon = R.drawable.settings_outline,
        focusIcon = R.drawable.settings_filled,
        contentDescription = R.string.setting
    ),
    HOME(
        route = "home", label = R.string.home,
        icon = R.drawable.home_outline,
        focusIcon = R.drawable.home_filled,
        contentDescription = R.string.home
    ),
    ABOUT(
        route = "about", label = R.string.about,
        icon = R.drawable.info_outline,
        focusIcon = R.drawable.info_filled,
        contentDescription = R.string.home
    )
}
