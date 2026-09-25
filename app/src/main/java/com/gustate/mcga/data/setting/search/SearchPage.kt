package com.gustate.mcga.data.setting.search

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.SettingPage
import com.gustate.mcga.data.setting.search.group.RecomAppGroup

/**
 * 搜索页设置
 */
object SearchPage : SettingPage(
    route = "search",
    command = "su -c pkill -f com.heytap.quicksearchbox",
    icon = R.drawable.ic_search,
    title = R.string.hook_search,
    summary = null,
    dependency = null,
    children = listOf(
        RecomAppGroup
    )
)