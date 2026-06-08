package com.gustate.mcga.data.setting.systemui.group.tile

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 *
 */
object QsMediaTileGroup : SettingGroup(
    title = R.string.qs_media_tile,
    dependency = null,
    children = listOf(
        EnableCustomQsMediaTile,
        QsMediaTileCornerRadius
    )
)

object EnableCustomQsMediaTile : BooleanSetting(
    key = "enable_custom_qs_media_tile",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    summary = null,
    dependency = null
)

object QsMediaTileCornerRadius : FloatSetting(
    key = "qs_media_tile_corner_radius",
    min = 0f,
    max = 96f,
    default = 24f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableCustomQsMediaTile
)