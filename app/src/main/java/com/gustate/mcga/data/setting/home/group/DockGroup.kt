package com.gustate.mcga.data.setting.home.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * Dock 设置组声明
 */
object DockGroup : SettingGroup(
    title = R.string.dock_bar,
    children = listOf(
        EnableDockBkg,
        EnableDockBlur,
        DockBlurRadius,
        DockCornerRadius
    )
)

/**
 * 启用 Dock 背景
 */
private object EnableDockBkg : BooleanSetting(
    key = "dock_bkg",
    default = false,
    icon = R.drawable.dock_to_bottom_filled,
    title = R.string.force_dock_bkg,
    summary = null,
    dependency = null
)

/**
 * 启用 Dock 模糊
 */
private object EnableDockBlur : BooleanSetting(
    key = "dock_blur",
    default = false,
    icon = R.drawable.blur_on,
    title = R.string.force_dock_blur,
    summary = null,
    dependency = EnableDockBkg
)

/**
 * Dock 背景模糊半径
 */
private object DockBlurRadius : FloatSetting(
    key = "dock_blur_radius",
    default = 800f,
    min = 0f,
    max = 1999f,
    icon = R.drawable.opacity,
    title = R.string.bkg_blur_radius,
    summary = null,
    dependency = EnableDockBkg
)

/**
 * Dock 背景圆角半径
 */
private object DockCornerRadius : FloatSetting(
    key = "dock_corner_radius",
    default = 28f,
    min = 0f,
    max = 99f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableDockBkg
)