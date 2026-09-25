package com.gustate.mcga.data.setting.home.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 抽屉设置组声明
 */
object DrawerGroup : SettingGroup(
    title = R.string.drawer,
    children = listOf(
        HideDrawerName
    )
)

/**
 * 隐藏抽屉应用名称
 */
private object HideDrawerName : BooleanSetting(
    key = "hide_app_name",
    default = false,
    icon = R.drawable.receipt_long_off,
    title = R.string.hide_drawer_app_name,
    summary = null,
    dependency = null
)