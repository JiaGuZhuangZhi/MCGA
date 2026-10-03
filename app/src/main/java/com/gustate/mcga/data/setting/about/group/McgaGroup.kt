package com.gustate.mcga.data.setting.about.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.LinkSetting
import com.gustate.mcga.data.setting.base.SettingGroup

object McgaGroup : SettingGroup(
    title = R.string.about,
    children = listOf(
        AppAbout,
        Developer
    )
)

private object AppAbout : LinkSetting(
    url = "https://github.com/JiaGuZhuangZhi/MCGA",
    title = R.string.app_name,
    summary = R.string.app_des,
    icon = R.drawable.mobile_share,
)

private object Developer : LinkSetting(
    url = "https://github.com/JiaGuZhuangZhi",
    title = R.string.gustate,
    summary = R.string.gustate_des,
    icon = R.drawable.architecture
)