package com.gustate.mcga.xposed.aod

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.AodKeys
import com.gustate.mcga.xposed.aod.feature.PanoramicHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class AodHook : XposedModule() {

    private val panoramicHook = PanoramicHook()

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        // 以下内容仅 Hook 息屏 (com.oplus.aod)
        if (param.packageName != "com.oplus.aod") return

        // 获取 Lsposed 远程配置
        val prefs = getRemotePreferences("mcga_prefs")

        // 应用配置
        applyAodFeature(param = param, prefs = prefs)

    }

    private fun applyAodFeature(
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableAllDayAodSettings = prefs.getBoolean(
            AodKeys.ENABLE_ALL_DAY_AOD_SETTINGS, false
        )
        if (enableAllDayAodSettings)
            panoramicHook.enableAllDayAodSettings(module = this, param = param)
    }
}