package com.gustate.mcga.xposed.home

import com.gustate.mcga.data.keys.HomeKeys
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.home.feature.DockBlur
import com.gustate.mcga.xposed.home.feature.DrawerAppName
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

object HomeHook {
    fun applySearchFeature(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 以下内容仅 Hook 全局搜索 (com.android.launcher)
        if (lpparam.packageName != "com.android.launcher") return
        val prefs = XSharedPreferences(
            "com.gustate.mcga",
            "xposed_prefs"
        )
        prefs.makeWorldReadable()
        val enableBlur = prefs.getBoolean(
            HomeKeys.ENABLE_DOCK_BLUR,
            false
        )
        val goneDrawerAppName = prefs.getBoolean(
            HomeKeys.HIDE_DRAWER_NAME,
            false
        )
        if (enableBlur) DockBlur.enableDockBlur(lpparam)
        if (goneDrawerAppName) DrawerAppName.goneDrawerAppName(lpparam)
    }
}