package com.gustate.mcga.data.setting.search

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.SettingPage

/**
 * 搜索页设置
 */
object SearchPage : SettingPage(
    command = "su -c pkill -f com.heytap.quicksearchbox",
    icon = R.drawable.ic_search,
    title = R.string.hook_search,
    summary = null,
    dependency = null,
    children = listOf(
        HideRecomAppName,
        FixRecomCardHeight
    )
)

/**
 * 隐藏推荐应用名称
 */
object HideRecomAppName : BooleanSetting(
    key = "hide_recom_app_name",
    default = false,
    icon = R.drawable.receipt_long_off,
    title = R.string.hide_advice_app_name,
    summary = null,
    dependency = null
)

/**
 * 调整推荐应用卡片高度
 */
object FixRecomCardHeight : BooleanSetting(
    key = "fix_recom_card_height",
    default = false,
    icon = R.drawable.height,
    title = R.string.adjust_advice_app_card_height,
    summary = null,
    dependency = null
)