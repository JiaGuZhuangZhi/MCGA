package com.gustate.mcga.xposed.aod.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 息屏 (AOD) 全天候设置功能 Hook
 */
class PanoramicHook {

    companion object {
        const val AOD_LOG = "息屏"
    }

    /**
     * 在设置中启用全天候息屏选项
     * * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun enableAllDayAodSettings(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        try {
            // 获取目标类与方法
            val settingsUtilsClass = loadClass(
                className = "com.oplus.aod.util.SettingsUtils",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = AOD_LOG,
                message = "❌ 未获取到 OplusQSResizeableTileView 类"
            )
            val method = settingsUtilsClass.getDeclaredMethod(
                "getKeyAodAllDaySupportSettings",
                Context::class.java,
                Int::class.javaPrimitiveType
            )
            // 拦截并强制返回 1
            module.hook(method).intercept {
                log(
                    module = module, tag = AOD_LOG,
                    message = "✅ 成功启用全天候息屏设置 (SettingsUtils)"
                )
                // 直接返回 1，不执行原逻辑
                1
            }
        } catch (e: Exception) {
            log(
                module = module, tag = AOD_LOG,
                message = "❌ 启用全天候息屏设置失败: ${e.message}"
            )
        }
    }
}