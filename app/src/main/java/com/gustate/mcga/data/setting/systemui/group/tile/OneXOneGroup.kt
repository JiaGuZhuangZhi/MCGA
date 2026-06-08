package com.gustate.mcga.data.setting.systemui.group.tile

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.IntSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 1*1 磁贴设置组
 * @param title 标题
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
object OneXOneGroup : SettingGroup(
    title = R.string.qs_1x1_tile,
    dependency = null,
    children = listOf(
        EnableCustomQsTileOneXOne,
        QsTileOneXOneCornerRadius,
        QsTileOneXOneRowColumns
    )
)

/**
 * 启用自定义 1*1 磁贴设置
 */
object EnableCustomQsTileOneXOne : BooleanSetting(
    key = "enableCustomQsTileOneXOne",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    summary = null,
    dependency = null
)

/**
 * 1*1 磁贴背景圆角
 */
object QsTileOneXOneCornerRadius : FloatSetting(
    key = "qs_tile_1x1_corner_radius",
    default = 24f,
    min = 0f,
    max = 96f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableCustomQsTileOneXOne
)

/**
 * 1*1 磁贴行数
 */
object QsTileOneXOneRowColumns : IntSetting(
    key = "qs_tile_1x1_row_columns",
    default = 4,
    min = 0,
    max = 8,
    icon = R.drawable.table_rows_narrow,
    title = R.string.list_row_count,
    summary = null,
    dependency = EnableCustomQsTileOneXOne
)