package com.gustate.mcga.data.setting.systemui.tile

import android.content.SharedPreferences
import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.ColorSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.SettingExt.getValue
import com.gustate.mcga.data.setting.base.SettingExt.listenConfig
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 2*1 磁贴设置组
 */
object TwoXOneGroup : SettingGroup(
    title = R.string.qs_resizeable_tile,
    dependency = null,
    children = listOf(
        EnableCustomQsResizeableTile,
        QsResizeableTileCornerRadius,
        QsTwoXOneTileFillStateFullBkg,
        QsTwoXOneTileHideIconBkg,
        QsTwoXOneTileIconSize,
        QsTwoXOneTileInactiveTitleColor,
        QsTwoXOneTileActiveTitleColor,
        QsTwoXOneTileInactiveDesColor,
        QsTwoXOneTileActiveDesColor
    )
)

/**
 * 是否启用自定义控制中心 2*1 磁贴设置
 */
private object EnableCustomQsResizeableTile : BooleanSetting(
    key = "enable_custom_qs_resizeable_tile",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    summary = null,
    dependency = null
)

/**
 * 修改控制中心 2*1 磁贴圆角半径
 */
private object QsResizeableTileCornerRadius : FloatSetting(
    key = "qs_resizeable_tile_corner_radius",
    default = 24f,
    min = 0f,
    max = 96f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 使磁贴状态填满控制中心 2*1 磁贴
 */
private object QsTwoXOneTileFillStateFullBkg : BooleanSetting(
    key = "qs_two_x_one_tile_fill_state_full_bkg",
    default = false,
    icon = R.drawable.format_color_fill,
    title = R.string.fill_the_tile_state_with_tiles,
    summary = null,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 隐藏控制中心 2*1 磁贴图标背景 (状态)
 */
private object QsTwoXOneTileHideIconBkg : BooleanSetting(
    key = "qs_two_x_one_tile_hide_icon_bkg",
    default = false,
    icon = R.drawable.grid_off_filled,
    title = R.string.hide_the_background_of_tile_icons,
    summary = null,
    dependency = EnableCustomQsResizeableTile
)


/**
 * 修改控制中心 2*1 磁贴图标大小
 */
private object QsTwoXOneTileIconSize : FloatSetting(
    key = "qs_two_x_one_tile_icon_size",
    default = 28f,
    min = 0f,
    max = 42f,
    icon = R.drawable.panels_outline,
    title = R.string.change_tile_icon_size,
    summary = R.string.affect_entire_control_center,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 控制中心 2*1 磁贴非激活/不可用状态下标题颜色
 */
private object QsTwoXOneTileInactiveTitleColor : ColorSetting(
    key = "qs_two_x_one_tile_inactive_title_color",
    default = 0XE6FFFFFF.toInt(),
    icon = R.drawable.opacity,
    title = R.string.title_color_in_inactive_state_of_tile,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 控制中心 2*1 磁贴非激活/不可用状态标签颜色
 */
private object QsTwoXOneTileActiveTitleColor : ColorSetting(
    key = "qs_two_x_one_tile_active_title_color",
    default = 0XE6000000.toInt(),
    icon = R.drawable.opacity,
    title = R.string.title_color_in_active_state_of_tile,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 控制中心 2*1 磁贴非激活/不可用状态标签颜色
 */
private object QsTwoXOneTileInactiveDesColor : ColorSetting(
    key = "qs_two_x_one_tile_inactive_des_color",
    default = 0X89FFFFFF.toInt(),
    icon = R.drawable.opacity,
    title = R.string.des_color_in_inactive_state_of_tile,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 控制中心 2*1 磁贴非激活/不可用状态标签颜色
 */
private object QsTwoXOneTileActiveDesColor : ColorSetting(
    key = "qs_two_x_one_tile_active_des_color",
    default = 0X89000000.toInt(),
    icon = R.drawable.opacity,
    title = R.string.des_color_in_active_state_of_tile,
    dependency = EnableCustomQsResizeableTile
)

/**
 * 从 SharedPreferences 读取 2*1 磁贴的完整配置
 * 并填充到 [TwoXOneConfig] 中
 */
fun SharedPreferences.readTwoXOneConfig(): TwoXOneConfig = TwoXOneConfig(
    enableCustomQsResizeableTile =
        getValue(setting = EnableCustomQsResizeableTile),
    qsResizeableTileCornerRadius =
        getValue(setting = QsResizeableTileCornerRadius),
    qsTwoXOneTileFillStateFullBkg =
        getValue(setting = QsTwoXOneTileFillStateFullBkg),
    qsTwoXOneTileHideIconBkg =
        getValue(setting = QsTwoXOneTileHideIconBkg),
    qsTwoXOneTileIconSize =
        getValue(setting = QsTwoXOneTileIconSize),
    qsTwoXOneTileInactiveTitleColor =
        getValue(setting = QsTwoXOneTileInactiveTitleColor),
    qsTwoXOneTileActiveTitleColor =
        getValue(setting = QsTwoXOneTileActiveTitleColor),
    qsTwoXOneTileInactiveDesColor =
        getValue(setting = QsTwoXOneTileInactiveDesColor),
    qsTwoXOneTileActiveDesColor =
        getValue(setting = QsTwoXOneTileActiveDesColor)
)

/**
 * 监听 2*1 磁贴配置变化
 * @param onChanged 配置变化回调
 * @return [SharedPreferences.OnSharedPreferenceChangeListener]
 */
fun SharedPreferences.listenTwoXOneConfig(
    onChanged: (TwoXOneConfig) -> Unit
): SharedPreferences.OnSharedPreferenceChangeListener =
    listenConfig(group = TwoXOneGroup) {
        onChanged(readTwoXOneConfig())
    }

/**
 * 2*1 磁贴设置的解析结果
 */
data class TwoXOneConfig(
    val enableCustomQsResizeableTile: Boolean?,
    val qsResizeableTileCornerRadius: Float?,
    val qsTwoXOneTileFillStateFullBkg: Boolean?,
    val qsTwoXOneTileHideIconBkg: Boolean?,
    val qsTwoXOneTileIconSize: Float?,
    val qsTwoXOneTileInactiveTitleColor: Int?,
    val qsTwoXOneTileActiveTitleColor: Int?,
    val qsTwoXOneTileInactiveDesColor: Int?,
    val qsTwoXOneTileActiveDesColor: Int?
)
