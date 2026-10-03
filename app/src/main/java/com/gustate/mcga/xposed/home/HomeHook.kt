/**
 * MCGA (Make Color Great Again) - A Free and Open-Source Xposed Module for ColorOS Users
 *
 * Copyright (C) 2026 Zhuangzhi Meng (Gustate XiaoMeng)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gustate.mcga.xposed.home

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.HomeKeys.CLEAR_ALL_BUTTON
import com.gustate.mcga.data.keys.HomeKeys.HIDE_DRAWER_NAME
import com.gustate.mcga.data.setting.home.group.listenDockConfig
import com.gustate.mcga.data.setting.home.group.listenHomeBlurConfig
import com.gustate.mcga.data.setting.home.group.readDockConfig
import com.gustate.mcga.data.setting.home.group.readHomeBlurConfig
import com.gustate.mcga.xposed.base.HookContext
import com.gustate.mcga.xposed.home.feature.DockHook
import com.gustate.mcga.xposed.home.feature.DrawerHook
import com.gustate.mcga.xposed.home.feature.HomeBlurHook
import com.gustate.mcga.xposed.home.feature.HomeDockHook
import com.gustate.mcga.xposed.home.feature.RecentsHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object HomeHook {


    const val HOME_DOCK = "桌面 Dock 栏"
    const val HOME_BLUR = "桌面高斯模糊"

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
        /*val enableDockBkg = prefs.getBoolean(ENABLE_DOCK_BKG, false)
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
        }*/
        val homeBlurHook = HomeBlurHook(
            hookContext = HookContext(
                module = module,
                param = param,
                classLoader = param.classLoader
            )
        )
        homeBlurHook.install(
            config = prefs
                .readHomeBlurConfig()
        )
        prefs.listenHomeBlurConfig { newConfig ->
            homeBlurHook.update(
                config = newConfig
            )
        }
        val dockHook = HomeDockHook(
            hookContext = HookContext(
                module = module,
                param = param,
                classLoader = param.classLoader
            )
        )
        dockHook.install(
            config = prefs
                .readDockConfig()
        )
        prefs.listenDockConfig { newConfig ->
            dockHook.update(
                config = newConfig
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