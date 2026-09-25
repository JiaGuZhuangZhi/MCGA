package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ClassHelper.setAnyField
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class PanoramicHook {

    companion object {
        private const val ALL_DAY_AOD_LOG = "全天候全景息屏"
    }

    fun hookPanoramicAodAllDay(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {

        // 加载 SmoothTransitionController 类
        val clazz = loadClass(
            name = "com.oplus.systemui.aod.display." +
                    $$"SmoothTransitionController$Companion",
            loader = param.classLoader
        )
        // Hook getInstance 方法
        val getInstanceMethod = clazz.getAnyMethod(
            methodName = "getInstance",
            parameterTypes = arrayOf(Context::class.java)
        )
        module.hook(getInstanceMethod).intercept { chain ->
            // 执行原逻辑
            val instance = chain.proceed()
            if (instance != null) {
                try {
                    // 反射修改字段
                    instance.setAnyField(
                        fieldName = "isSupportPanoramicAllDay",
                        value = true
                    )
                    instance.setAnyField(
                        fieldName = "isSupportPanoramicAllDayByPanelFeature",
                        value = true
                    )
                } catch (e: Exception) {
                    log(
                        module = module, tag = ALL_DAY_AOD_LOG,
                        message = "❌ 修改 AOD 字段失败: ${e.message}"
                    )
                }
            }
            return@intercept instance
        }
    }
}