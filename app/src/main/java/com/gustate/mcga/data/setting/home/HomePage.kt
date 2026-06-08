package com.gustate.mcga.data.setting.home

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingPage

/**
 * Home / Launcher 页面声明
 */
object HomePage : SettingPage(
    command = "su -c pkill -f com.android.launcher",
    icon = R.drawable.ic_home,
    title = R.string.hook_launcher,
    summary = null,
    dependency = null,
    children = listOf(
        EnableDockBkg,
        EnableDockBlur,
        DockBlurRadius,
        DockCornerRadius,
        HideDrawerName,
        ClearAllButton
    )
)

/**
 * 启用 Dock 背景
 */
object EnableDockBkg : BooleanSetting(
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
object EnableDockBlur : BooleanSetting(
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
object DockBlurRadius : FloatSetting(
    key = "dock_blur_radius",
    default = 800f,
    min = 0f,
    max = 1999f,
    icon = R.drawable.opacity,
    title = R.string.bkg_blur_radius,
    summary = null,
    dependency = EnableDockBkg // 或者 EnableDockBkg || EnableDockBlur 你 UI 层控制
)

/**
 * Dock 背景圆角半径
 */
object DockCornerRadius : FloatSetting(
    key = "dock_corner_radius",
    default = 28f,
    min = 0f,
    max = 99f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableDockBkg
)

/**
 * 隐藏抽屉应用名称
 */
object HideDrawerName : BooleanSetting(
    key = "hide_app_name",
    default = false,
    icon = R.drawable.receipt_long_off,
    title = R.string.hide_drawer_app_name,
    summary = null,
    dependency = null
)

/**
 * 修改 “清除所有应用” 按钮
 */
object ClearAllButton : BooleanSetting(
    key = "clear_all_button",
    default = false,
    icon = R.drawable.family_history,
    title = R.string.modify_the_clear_all_apps_button,
    summary = null,
    dependency = null
)