package com.gustate.mcga.data.setting.systemui.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 控制中心整体布局设置组
 * @param title 标题
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
object QsPanelGroup : SettingGroup(
    title = R.string.qs_panel,
    dependency = null,
    children = listOf(
        EnableCustomQsPanelLayout,
        QsPanelStatusBarMarginTop,
        QsPanelCellHeight
    )
)

/**
 * 启用自定义控制中心整体布局设置
 */
object EnableCustomQsPanelLayout : BooleanSetting(
    key = "enable_custom_qs_panel_layout",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings
)

/**
 * 控制中心状态栏顶部边距
 */
object QsPanelStatusBarMarginTop : FloatSetting(
    key = "qs_panel_status_bar_margin_top",
    default = 18f,
    min = 8f,
    max = 64f,
    icon = R.drawable.rounded_corner,
    title = R.string.qs_panel_status_bar_margin_top,
    summary = R.string.qs_panel_status_bar_margin_top_tips,
    dependency = EnableCustomQsPanelLayout
)

/**
 * 控制中心面板单元格高度
 */
object QsPanelCellHeight : FloatSetting(
    key = "qs_panel_cell_height",
    default = 76f,
    min = 48f,
    max = 96f,
    icon = R.drawable.height,
    title = R.string.qs_panel_cell_height,
    summary = null,
    dependency = EnableCustomQsPanelLayout
)