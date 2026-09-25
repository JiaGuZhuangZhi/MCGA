package com.gustate.mcga.data.setting.search.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 推荐应用设置组声明
 */
object RecomAppGroup : SettingGroup(
    title = R.string.recom_apps,
    children = listOf(
        HideRecomAppName,
        FixRecomCardHeight
    )
)

/**
 * 隐藏推荐应用名称
 */
private object HideRecomAppName : BooleanSetting(
    key = "hide_recommend_app_name",
    default = false,
    icon = R.drawable.receipt_long_off,
    title = R.string.hide_advice_app_name,
    summary = null,
    dependency = null
)

/**
 * 调整推荐应用卡片高度
 */
private object FixRecomCardHeight : BooleanSetting(
    key = "fix_recommend_card_height",
    default = false,
    icon = R.drawable.height,
    title = R.string.adjust_advice_app_card_height,
    summary = null,
    dependency = null
)