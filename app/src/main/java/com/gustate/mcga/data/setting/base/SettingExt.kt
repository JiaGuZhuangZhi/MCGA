package com.gustate.mcga.data.setting.base

import android.content.SharedPreferences

object SettingExt {
    @Suppress("UNCHECKED_CAST")
    fun <T> SharedPreferences.getValue(setting: SettingNode): T = when (setting) {
        is BooleanSetting -> getBoolean(setting.key, setting.default)
        is FloatSetting -> getFloat(setting.key, setting.default)
        is IntSetting -> getInt(setting.key, setting.default)
        is ColorSetting -> getInt(setting.key, setting.default)
        else -> Unit
    } as T
}