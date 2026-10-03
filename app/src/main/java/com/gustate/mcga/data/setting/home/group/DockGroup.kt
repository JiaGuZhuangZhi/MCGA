/**
 * MCGA (Make Color Great Again) - A Free and Open-Source Xposed Module for ColorOS Users
 *
 * Copyright (C) 2026 Zhuangzhi Meng (Gustate XiaoMeng)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gustate.mcga.data.setting.home.group

import android.content.SharedPreferences
import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.IntSetting
import com.gustate.mcga.data.setting.base.SettingExt.getValue
import com.gustate.mcga.data.setting.base.SettingExt.listenConfig
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * Dock 设置组声明
 */
object DockGroup : SettingGroup(
    title = R.string.dock_bar,
    children = listOf(
        EnableDockBkg,
        EnableDockBlur,
        DockBlurRadius,
        DockCornerRadius
    )
)

/**
 * 启用 Dock 背景
 */
private object EnableDockBkg : BooleanSetting(
    key = "dock_bkg",
    default = false,
    icon = R.drawable.dock_to_bottom_filled,
    title = R.string.force_dock_bkg,
    summary = null,
    dependency = null
)

/**
 * 启用 Dock 模糊
 */
private object EnableDockBlur : BooleanSetting(
    key = "dock_blur",
    default = false,
    icon = R.drawable.blur_on,
    title = R.string.force_dock_blur,
    summary = null,
    dependency = EnableDockBkg
)

/**
 * Dock 背景模糊半径
 */
object DockBlurRadius : IntSetting(
    key = "dock_blur_radius",
    default = 800,
    min = 0,
    max = 1999,
    icon = R.drawable.opacity,
    title = R.string.bkg_blur_radius,
    summary = null,
    dependency = EnableDockBkg
)

/**
 * Dock 背景圆角半径
 */
private object DockCornerRadius : FloatSetting(
    key = "dock_corner_radius",
    default = 28f,
    min = 0f,
    max = 99f,
    icon = R.drawable.rounded_corner,
    title = R.string.bkg_corner_radius,
    summary = null,
    dependency = EnableDockBkg
)

/**
 * 从 SharedPreferences 读取 Dock 的完整配置
 * 并填充到 [DockConfig] 中
 */
fun SharedPreferences.readDockConfig(): DockConfig = DockConfig(
    enableDockBkg = getValue(setting = EnableDockBkg),
    enableDockBlur = getValue(setting = EnableDockBlur),
    dockBlurRadius = getValue(setting = DockBlurRadius),
    dockCornerRadius = getValue(setting = DockCornerRadius)
)

/**
 * 监听 Dock 配置变化
 * @param onChanged 配置变化回调
 * @return [SharedPreferences.OnSharedPreferenceChangeListener]
 */
fun SharedPreferences.listenDockConfig(
    onChanged: (DockConfig) -> Unit
): SharedPreferences.OnSharedPreferenceChangeListener =
    listenConfig(group = DockGroup) {
        onChanged(readDockConfig())
    }

/**
 * Dock 设置的解析结果
 */
data class DockConfig(
    val enableDockBkg: Boolean?,
    val enableDockBlur: Boolean?,
    val dockBlurRadius: Int?,
    val dockCornerRadius: Float?
)