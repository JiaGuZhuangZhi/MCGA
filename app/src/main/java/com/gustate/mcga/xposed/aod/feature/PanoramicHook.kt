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
        val settingsUtilsClass = loadClass(
            "com.oplus.aod.util.SettingsUtils",
            param.classLoader
        ) ?: return

        try {
            // 获取目标方法：getKeyAodAllDaySupportSettings(Context, int)
            val method = settingsUtilsClass.getDeclaredMethod(
                "getKeyAodAllDaySupportSettings",
                Context::class.java,
                Int::class.javaPrimitiveType
            )

            // 拦截并强制返回 1 (支持全天候)
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

/*package com.gustate.mcga.xposed.aod.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object PanoramicHook {
    fun enableAllDayAodSettings(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val oplusHotseatClass = XposedHelpers.findClass(
                "com.oplus.aod.util.SettingsUtils",
                lpparam.classLoader
            )
            XposedHelpers.findAndHookMethod(
                oplusHotseatClass,
                "getKeyAodAllDaySupportSettings",
                Context::class.java,
                Int::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        log(tag = "息屏", message = "✅ 成功启用全天候息屏设置")
                        param.result = 1
                    }
                }
            )
        } catch (e: Exception) {
            log(
                message = "❌ 启用全天候息屏设置失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看",
                tag = "息屏"
            )
        }
    }
}*/