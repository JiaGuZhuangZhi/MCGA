package com.gustate.mcga.main.navgation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gustate.mcga.R

enum class Destination(
    @param:StringRes @field:StringRes
    val label: Int,
    @param:StringRes @field:StringRes
    val navLabel: Int = label,
    @param:DrawableRes @field:DrawableRes
    val icon: Int,
    @param:DrawableRes @field:DrawableRes
    val focusIcon: Int,
    @param:StringRes @field:StringRes
    val contentDescription: Int
) {
    HOME(
        label = R.string.app_name,
        navLabel = R.string.home,
        icon = R.drawable.home_outline,
        focusIcon = R.drawable.home_filled,
        contentDescription = R.string.home
    ),
    SETTING(
        label = R.string.setting,
        icon = R.drawable.settings_outline,
        focusIcon = R.drawable.settings_filled,
        contentDescription = R.string.setting
    ),
    ABOUT(
        label = R.string.about,
        icon = R.drawable.info_outline,
        focusIcon = R.drawable.info_filled,
        contentDescription = R.string.home
    )
}
