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
 * @param command 右上角图标的命令
 * @param icon 图标
 * @param summary 摘要
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
abstract class SettingPage(
    val command: String?,
    @field:DrawableRes
    val icon: Int,
    @field:StringRes
    val summary: Int? = null,
    @field:StringRes title: Int,
    dependency: BooleanSetting? = null,
    children: List<SettingNode>
) : SettingGroup(
    title = title,
    dependency = dependency,
    children = children
)