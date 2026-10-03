package com.gustate.mcga.data.setting.home

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.SettingPage
import com.gustate.mcga.data.setting.home.group.DockGroup
import com.gustate.mcga.data.setting.home.group.DrawerGroup
import com.gustate.mcga.data.setting.home.group.HomeBlurGroup
import com.gustate.mcga.data.setting.home.group.RecentlyGroup

/**
 * Home / Launcher 页面声明
 */
object HomePage : SettingPage(
    route = "home",
    command = "su -c pkill -f com.android.launcher",
    icon = R.drawable.ic_home,
    title = R.string.hook_launcher,
    summary = null,
    dependency = null,
    children = listOf(
        HomeBlurGroup,
        DockGroup,
        DrawerGroup,
        RecentlyGroup
    )
)