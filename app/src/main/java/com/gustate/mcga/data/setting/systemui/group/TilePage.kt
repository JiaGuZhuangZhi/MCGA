package com.gustate.mcga.data.setting.systemui.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.SettingPage
import com.gustate.mcga.data.setting.systemui.group.tile.OneXOneGroup
import com.gustate.mcga.data.setting.systemui.group.tile.QsMediaTileGroup
import com.gustate.mcga.data.setting.systemui.group.tile.QsSliderTileGroup
import com.gustate.mcga.data.setting.systemui.group.tile.TwoXOneGroup

object TilePage : SettingPage(
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