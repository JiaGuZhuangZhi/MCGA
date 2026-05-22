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
sealed class SettingItem<T>(
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
sealed class BooleanSetting(
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
sealed class ColorSetting(
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
sealed class FloatSetting(
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
sealed class IntSetting(
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
    dependency = dependency
)