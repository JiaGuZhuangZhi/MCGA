package com.gustate.mcga.xposed.aod

import com.gustate.mcga.data.keys.AodKeys
import com.gustate.mcga.xposed.aod.feature.AllDay
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodHook {
    fun applyAodFeature(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 以下内容仅 Hook 息屏 (com.oplus.aod)
        if (lpparam.packageName != "com.oplus.aod") return
        val prefs = XSharedPreferences(
            "com.gustate.mcga",
            "xposed_prefs"
        )
        prefs.makeWorldReadable()
        val enableAllDayAodSettings = prefs.getBoolean(
            AodKeys.ENABLE_ALL_DAY_AOD_SETTINGS,
            false
        )
        if (enableAllDayAodSettings) AllDay.enableAllDayAodSettings(lpparam)
    }
}