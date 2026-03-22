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

class HomeHook : XposedModule() {

    // 实例化相关 Feature 类
    private val dockHook = DockHook()
    private val drawerHook = DrawerHook()
    private val recentsHook = RecentsHook()

    /**
     * 成功实例化软件包加载器
     * @param param 正在装载的软件包信息
     */
    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        // 以下代码仅 Hook 系统桌面 (com.android.launcher)
        if (param.packageName != "com.android.launcher") return

        // 获取 Lsposed 远程配置
        val prefs = getRemotePreferences("mcga_prefs")

        // 应用配置
        applyDockFeature(param = param, prefs = prefs)
        applyDrawerFeature(param = param, prefs = prefs)
        applyRecentsFeature(param = param, prefs = prefs)

    }

    /**
     * 应用 Dock 栏配置
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyDockFeature(
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableDockBkg = prefs.getBoolean(ENABLE_DOCK_BKG, false)
        if (enableDockBkg) {
            val enableDockBlur = prefs.getBoolean(ENABLE_DOCK_BLUR, true)
            val blurRadius = prefs.getInt(DOCK_BLUR_RADIUS, 800)
            val cornerRadius = prefs.getFloat(DOCK_CORNER_RADIUS, 28f)
            dockHook.hookDock(
                module = this,
                param = param,
                enableDockBlur = enableDockBlur,
                blurRadius = blurRadius,
                cornerRadius = cornerRadius
            )
        }
    }

    /**
     * 应用抽屉配置
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyDrawerFeature(
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val goneDrawerAppName = prefs.getBoolean(HIDE_DRAWER_NAME, false)
        if (goneDrawerAppName) {
            drawerHook.hideDrawerAppName(
                module = this,
                param = param
            )
        }
    }

    /**
     * 应用最近任务配置
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyRecentsFeature(
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val customizeClearAllBtn = prefs.getBoolean(CLEAR_ALL_BUTTON, false)
        if (customizeClearAllBtn) {
            recentsHook.customizeClearAllButton(
                module = this,
                param = param
            )
        }
    }

}