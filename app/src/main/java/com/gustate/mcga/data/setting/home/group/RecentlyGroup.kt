package com.gustate.mcga.data.setting.home.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * 最近任务设置组声明
 */
object RecentlyGroup : SettingGroup(
    title = R.string.recently,
    children = listOf(
        ClearAllButton
    )
)

/**
 * 修改 “清除所有应用” 按钮
 */
private object ClearAllButton : BooleanSetting(
    key = "clear_all_button",
    default = false,
    icon = R.drawable.family_history,
    title = R.string.modify_the_clear_all_apps_button,
    summary = null,
    dependency = null
)