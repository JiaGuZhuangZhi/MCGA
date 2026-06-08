package com.gustate.mcga.data.setting.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 设置项基类
 * 用于提供一个设置项的基础类
 * 包括键值与 UI 相关属性
 * @param T 值类型
 * @param key 键
 * @param default 默认值
 * @param icon 图标
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 */
abstract class SettingItem<T>(
    // 键值
    val key: String,
    val default: T,
    // UI 元数据
    @field:DrawableRes
    val icon: Int,
    @field:StringRes
    val title: Int,
    @field:StringRes
    val summary: Int? = null,
    val summaryProvider: ((T) -> String)? = null,
    val dependency: BooleanSetting? = null
) : SettingNode

/**
 * Boolean 设置类
 * @param key 键
 * @param default 默认值
 * @param icon 图标
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 */
open class BooleanSetting(
    key: String,
    default: Boolean,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes summary: Int? = null,
    dependency: BooleanSetting? = null
) : SettingItem<Boolean>(
    key = key,
    default = default,
    icon = icon,
    title = title,
    summary = summary,
    summaryProvider = null,
    dependency = dependency
)

/**
 * 颜色设置类
 * @param key 键
 * @param default 默认值
 * @param icon 图标
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 */
open class ColorSetting(
    key: String,
    default: Int,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes summary: Int? = null,
    dependency: BooleanSetting? = null
) : SettingItem<Int>(
    key = key,
    default = default,
    icon = icon,
    title = title,
    summary = summary,
    summaryProvider = { it.toString() },
    dependency = dependency
)


/**
 * Float 设置类
 * @param min 最小值
 * @param max 最大值
 * @param key 键
 * @param default 默认值
 * @param icon 图标
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 */
open class FloatSetting(
    val min: Float,
    val max: Float,
    key: String,
    default: Float,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes summary: Int? = null,
    dependency: BooleanSetting? = null
) : SettingItem<Float>(
    key = key,
    default = default,
    icon = icon,
    title = title,
    summary = summary,
    summaryProvider = null,
    dependency = dependency
)

/**
 * Int 设置类
 * @param min 最小值
 * @param max 最大值
 * @param key 键
 * @param default 默认值
 * @param icon 图标
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 */
open class IntSetting(
    val min: Int,
    val max: Int,
    key: String,
    default: Int,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes summary: Int? = null,
    dependency: BooleanSetting? = null
) : SettingItem<Int>(
    key = key,
    default = default,
    icon = icon,
    title = title,
    summary = summary,
    summaryProvider = null,
    dependency = dependency
)

/**
 * 设置选项类
 * @param command 命令
 * @param icon 图标
 * @param title 标题
 * @param summaryProvider 摘要处理器
 * @param dependency 依赖 (启用条件)
 */
open class CommandSetting(
    val command: String? = null,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes summary: Int? = null,
    dependency: BooleanSetting? = null
) : SettingItem<Any>(
    key = "",
    default = "",
    icon = icon,
    title = title,
    summary = summary,
    summaryProvider = null,
    dependency = dependency
)