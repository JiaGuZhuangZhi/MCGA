package com.gustate.mcga.data.setting.systemui.tile

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.SettingPage

object TilePage : SettingPage(
    route = "systemui/tile",
    command = "su -c pkill -f com.android.systemui",
    icon = R.drawable.tile,
    title = R.string.tile,
    summary = null,
    dependency = null,
    children = listOf(
        OneXOneGroup,
        TwoXOneGroup,
        QsSliderTileGroup,
        QsMediaTileGroup
    )
)