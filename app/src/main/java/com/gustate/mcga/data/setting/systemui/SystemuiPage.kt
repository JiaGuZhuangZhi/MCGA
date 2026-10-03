package com.gustate.mcga.data.setting.systemui

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.aod.group.PanoramicGroup
import com.gustate.mcga.data.setting.base.SettingPage
import com.gustate.mcga.data.setting.systemui.group.QsDetailGroup
import com.gustate.mcga.data.setting.systemui.group.QsPanelGroup
import com.gustate.mcga.data.setting.systemui.group.TileGroup

object SystemuiPage : SettingPage(
    route = "systemui",
    command = "su -c pkill -f com.android.systemui",
    icon = R.drawable.ic_system_ui,
    title = R.string.systemui,
    summary = null,
    dependency = null,
    children = listOf(
        TileGroup,
        QsPanelGroup,
        QsDetailGroup,
        PanoramicGroup
    )
)