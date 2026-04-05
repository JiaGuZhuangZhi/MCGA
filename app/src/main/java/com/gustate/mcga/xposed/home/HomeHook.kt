package com.gustate.mcga.xposed.home

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.HomeKeys.CLEAR_ALL_BUTTON
import com.gustate.mcga.data.keys.HomeKeys.DOCK_BLUR_RADIUS
import com.gustate.mcga.data.keys.HomeKeys.DOCK_CORNER_RADIUS
import com.gustate.mcga.data.keys.HomeKeys.ENABLE_DOCK_BKG
import com.gustate.mcga.data.keys.HomeKeys.ENABLE_DOCK_BLUR
import com.gustate.mcga.data.keys.HomeKeys.HIDE_DRAWER_NAME
import com.gustate.mcga.xposed.home.feature.DockHook
import com.gustate.mcga.xposed.home.feature.DrawerHook
import com.gustate.mcga.xposed.home.feature.RecentsHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object HomeHook {

    // 实例化相关 Feature 类
    private val dockHook = DockHook()
    private val drawerHook = DrawerHook()
    private val recentsHook = RecentsHook()

    /**
     * 应用系统桌面 Hook 设置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyHomeFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 以下代码仅 Hook 系统桌面 (com.android.launcher)
        if (param.packageName != "com.android.launcher") return

        // 应用配置
        applyDockFeature(module = module, param = param, prefs = prefs)
        applyDrawerFeature(module = module, param = param, prefs = prefs)
        applyRecentsFeature(module = module, param = param, prefs = prefs)

    }

    /**
     * 应用 Dock 栏配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyDockFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableDockBkg = prefs.getBoolean(ENABLE_DOCK_BKG, false)
        if (enableDockBkg) {
            val enableDockBlur = prefs.getBoolean(ENABLE_DOCK_BLUR, true)
            val blurRadius = prefs.getInt(DOCK_BLUR_RADIUS, 800)
            val cornerRadius = prefs.getFloat(DOCK_CORNER_RADIUS, 28f)
            dockHook.hookDock(
                module = module,
                param = param,
                enableDockBlur = enableDockBlur,
                blurRadius = blurRadius,
                cornerRadius = cornerRadius
            )
        }
    }

    /**
     * 应用抽屉配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyDrawerFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val goneDrawerAppName = prefs.getBoolean(HIDE_DRAWER_NAME, false)
        if (goneDrawerAppName) {
            drawerHook.hideDrawerAppName(
                module = module,
                param = param
            )
        }
    }

    /**
     * 应用最近任务配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyRecentsFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val customizeClearAllBtn = prefs.getBoolean(CLEAR_ALL_BUTTON, false)
        if (customizeClearAllBtn) {
            recentsHook.customizeClearAllButton(
                module = module,
                param = param
            )
        }
    }

}