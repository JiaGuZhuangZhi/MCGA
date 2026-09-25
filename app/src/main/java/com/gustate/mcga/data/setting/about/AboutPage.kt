package com.gustate.mcga.data.setting.about

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.about.group.AcknowledgmentsGroup
import com.gustate.mcga.data.setting.about.group.GroupGroup
import com.gustate.mcga.data.setting.about.group.McgaGroup
import com.gustate.mcga.data.setting.base.SettingPage

object AboutPage : SettingPage(
    route = "aod",
    command = "su -c pkill -f com.android.systemui " +
            "&& su -c pkill -f com.oplus.aod",
    icon = R.drawable.aod,
    title = R.string.aod,
    summary = null,
    dependency = null,
    children = listOf(
        McgaGroup,
        GroupGroup,
        AcknowledgmentsGroup
    )
)