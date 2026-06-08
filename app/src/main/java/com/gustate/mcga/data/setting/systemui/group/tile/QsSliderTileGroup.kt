package com.gustate.mcga.data.setting.systemui.group.tile

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 滑动块磁贴设置组
 * @param title 标题
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
object QsSliderTileGroup : SettingGroup(
    title = R.string.qs_slider_tile,
    dependency = null,
    children = listOf(
        EnableCustomQsSliderTile,
        QsSliderTileCornerRadius
    )
)

/**
 * 启用对控制中心拖动条磁贴的修改
 */
object EnableCustomQsSliderTile : BooleanSetting(
    key = "enable_custom_qs_slider_tile",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    summary = null,
    dependency = null
)

/**
 * 控制中心拖动条磁贴圆角
 */
object QsSliderTileCornerRadius : FloatSetting(
    key = "qs_slider_tile_corner_radius",
    min = 0f,
    max = 96f,
    default = 24f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableCustomQsSliderTile
)