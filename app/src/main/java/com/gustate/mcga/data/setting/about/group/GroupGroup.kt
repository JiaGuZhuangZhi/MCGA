package com.gustate.mcga.data.setting.about.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.LinkSetting
import com.gustate.mcga.data.setting.base.SettingGroup

object GroupGroup : SettingGroup(
    title = R.string.group,
    children = listOf(
        QQ,
        Telegram,
        TelegramXiaomeng
    )
)

private object QQ : LinkSetting(
    url = "https://qm.qq.com/q/yPA0nIaF2g",
    title = R.string.qq_group,
    summary = null,
    icon = R.drawable.qq,
)

private object Telegram : LinkSetting(
    url = "https://t.me/+eYK0laXMEiMxMjc1",
    title = R.string.telegram_group,
    summary = null,
    icon = R.drawable.telegram,
)

private object TelegramXiaomeng : LinkSetting(
    url = "https://t.me/+1FHZSKBrZrU3MTg1",
    title = R.string.telegram_group_Xiaomeng,
    summary = null,
    icon = R.drawable.telegram,
)