package com.gustate.mcga.data.setting.systemui.tile

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * QS 媒体磁贴设置组
 */
object QsMediaTileGroup : SettingGroup(
    title = R.string.qs_media_tile,
    dependency = null,
    children = listOf(
        EnableCustomQsMediaTile,
        QsMediaTileCornerRadius
    )
)

/**
 * 启用 QS 媒体磁贴自定义设置
 */
private object EnableCustomQsMediaTile : BooleanSetting(
    key = "enable_custom_qs_media_tile",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    summary = null,
    dependency = null
)

/**
 * QS 媒体磁贴圆角大小
 */
private object QsMediaTileCornerRadius : FloatSetting(
    key = "qs_media_tile_corner_radius",
    min = 0f,
    max = 96f,
    default = 24f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableCustomQsMediaTile
)