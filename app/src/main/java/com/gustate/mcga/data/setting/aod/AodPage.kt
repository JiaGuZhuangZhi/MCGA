package com.gustate.mcga.data.setting.aod

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.aod.group.PanoramicGroup
import com.gustate.mcga.data.setting.base.SettingPage

/**
 * AOD 页面声明
 */
object AodPage : SettingPage(
    route = "aod",
    command = "su -c pkill -f com.android.systemui " +
            "&& su -c pkill -f com.oplus.aod",
    icon = R.drawable.aod,
    title = R.string.aod,
    summary = null,
    dependency = null,
    children = listOf(
        PanoramicGroup
    )
)