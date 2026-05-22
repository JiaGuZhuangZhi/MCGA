package com.gustate.mcga.data.setting.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Boolean 设置类
 * @param key 键
 * @param default 默认值
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