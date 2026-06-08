package com.gustate.mcga.data.setting.systemui.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.CommandSetting
import com.gustate.mcga.data.setting.base.SettingPage

/**
 * AOD 页面声明
 * @param command 右上角图标的命令
 * @param icon 图标
 * @param title 标题
 * @param summary 摘要
 * @param dependency 依赖 (启用条件)
 * @param children 子项
 */
object AodPage : SettingPage(
    command = "su -c pkill -f com.android.systemui " +
            "&& su -c pkill -f com.oplus.aod",
    icon = R.drawable.aod,
    title = R.string.aod,
    summary = null,
    dependency = null,
    children = listOf(
        EnableAodPanoramicAllDay,
        EnableAllDayAodSettings,
        ActivateAodDisplaySetting
    )
)

/**
 * 启用全天全景 AOD
 */
object EnableAodPanoramicAllDay : BooleanSetting(
    key = "enable_aod_panoramic_all_day",
    default = false,
    icon = R.drawable.aod_tablet,
    title = R.string.enable_all_day_panoramic_aod,
    summary = R.string.tip_all_day_panoramic_aod,
    dependency = null
)

/**
 * 启用全天全景 AOD 设置页
 */
object EnableAllDayAodSettings : BooleanSetting(
    key = "enable_all_day_aod_settings",
    default = false,
    icon = R.drawable.aod_tablet,
    title = R.string.enable_all_day_panoramic_aod,
    summary = R.string.tip_all_day_panoramic_aod,
    dependency = EnableAodPanoramicAllDay
)

/**
 * 启动全天全景 AOD 设置页
 */
object ActivateAodDisplaySetting : CommandSetting(
    command = "am start -n com.oplus.aod/" +
            "com.oplus.aod.activity." +
            "AodSettingsDisplayActivity" +
            " -f 0x10000000",
    icon = R.drawable.mobile_share,
    title = R.string.activate_aod_display_settings,
    summary = null,
    dependency = null
)