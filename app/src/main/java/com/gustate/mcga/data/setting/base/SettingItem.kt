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
)