package com.gustate.mcga.data.setting.systemui.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.SettingGroup
import com.gustate.mcga.data.setting.systemui.tile.TilePage

/**
 * 磁贴设置组声明
 */
object TileGroup : SettingGroup(
    title = R.string.tile,
    children = listOf(
        TilePage
    )
)