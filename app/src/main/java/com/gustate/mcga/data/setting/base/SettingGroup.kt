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

package com.gustate.mcga.data.setting.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 设置组
 * 提供包括设置项的列表
 * @param title 标题
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
abstract class SettingGroup(
    @field:StringRes
    val title: Int,
    val dependency: BooleanSetting? = null,
    val children: List<SettingNode>
) : SettingNode

/**
 * 设置页
 * 实际上是独立成页的设置组
 * @param route 导航路径
 * @param command 右上角图标的命令
 * @param icon 图标
 * @param summary 摘要
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
abstract class SettingPage(
    val route: String,
    val command: String?,
    @field:DrawableRes
    val icon: Int,
    @field:StringRes
    val summary: Int? = null,
    @field:StringRes title: Int,
    dependency: BooleanSetting? = null,
    children: List<SettingGroup>
) : SettingGroup(
    title = title,
    dependency = dependency,
    children = children
)