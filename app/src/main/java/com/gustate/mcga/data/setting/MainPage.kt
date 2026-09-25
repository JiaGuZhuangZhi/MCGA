package com.gustate.mcga.data.setting

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.aod.AodPage
import com.gustate.mcga.data.setting.base.SettingGroup
import com.gustate.mcga.data.setting.base.SettingPage
import com.gustate.mcga.data.setting.home.HomePage
import com.gustate.mcga.data.setting.search.SearchPage
import com.gustate.mcga.data.setting.systemui.SystemuiPage
import com.gustate.mcga.data.setting.wallet.WalletPage

object MainPage : SettingPage(
    route = "main",
    command = null,
    icon = R.drawable.settings_filled,
    title = R.string.setting,
    children = listOf(
        MainGroup,
        MoreGroup
    )
)

private object MainGroup : SettingGroup(
    title = R.string.main,
    children = listOf(
        HomePage,
        SystemuiPage,
        AodPage
    )
)

private object MoreGroup : SettingGroup(
    title = R.string.more,
    children = listOf(
        WalletPage,
        SearchPage
    )
)