package com.gustate.mcga.data.setting.aod.group

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.CommandSetting
import com.gustate.mcga.data.setting.base.SettingGroup

/**
 * Aod 设置组声明
 */
object PanoramicGroup : SettingGroup(
    title = R.string.panoramic_aod,
    children = listOf(
        EnableAodPanoramicAllDay,
        EnableAllDayAodSettings,
        ActivateAodDisplaySetting
    )
)

/**
 * 启用全天全景 AOD
 */
private object EnableAodPanoramicAllDay : BooleanSetting(
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
private object EnableAllDayAodSettings : BooleanSetting(
    key = "enable_all_day_aod_settings",
    default = false,
    icon = R.drawable.settings_panorama,
    title = R.string.enable_aod_display_settings,
    summary = R.string.tip_all_day_panoramic_aod,
    dependency = EnableAodPanoramicAllDay
)

/**
 * 启动全天全景 AOD 设置页
 */
private object ActivateAodDisplaySetting : CommandSetting(
    command = "am start -n com.oplus.aod/" +
            "com.oplus.aod.activity." +
            "AodSettingsDisplayActivity" +
            " -f 0x10000000",
    icon = R.drawable.mobile_share,
    title = R.string.activate_aod_display_settings,
    summary = null,
    dependency = null
)