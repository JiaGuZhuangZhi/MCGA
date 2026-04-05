package com.gustate.mcga.xposed.aod

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.AodKeys
import com.gustate.mcga.xposed.aod.feature.PanoramicHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object AodHook {

    // 实例化相关 Feature 类
    private val panoramicHook = PanoramicHook()

    /**
     * 应用息屏 Hook 设置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyAodFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 以下内容仅 Hook 息屏 (com.oplus.aod)
        if (param.packageName != "com.oplus.aod") return

        // 应用配置
        applyPanoramicFeature(module = module, param = param, prefs = prefs)

    }

    /**
     * 应用全景息屏配置配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyPanoramicFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableAllDayAodSettings = prefs.getBoolean(
            AodKeys.ENABLE_ALL_DAY_AOD_SETTINGS, false
        )
        if (enableAllDayAodSettings)
            panoramicHook.enableAllDayAodSettings(module = module, param = param)
    }
}