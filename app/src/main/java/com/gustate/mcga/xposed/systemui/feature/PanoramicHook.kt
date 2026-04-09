package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
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
            className = "com.oplus.systemui.aod.display.SmoothTransitionController\$Companion",
            classLoader = param.classLoader
        ) ?: throw NullPointerException("❌ 找不到 SmoothTransitionController 类")

        // 找到 getInstance 方法
        val getInstanceMethod = clazz
            .getDeclaredMethod(
                "getInstance",
                Context::class.java
            ) ?: throw NullPointerException("❌ 找不到 getInstance (取实例) 方法")
        // 开始 Hook
        module.hook(getInstanceMethod).intercept { chain ->
            // 执行原逻辑
            val instance = chain.proceed()
            if (instance != null) {
                try {
                    // 反射修改字段
                    instance.setAnyField(fieldName = "isSupportPanoramicAllDay", value = true)
                    instance.setAnyField(
                        fieldName = "isSupportPanoramicAllDayByPanelFeature", value = true
                    )
                } catch (e: Exception) {
                    log(
                        module = module, tag = ALL_DAY_AOD_LOG,
                        message = "❌ 修改 AOD 字段失败: ${e.message}"
                    )
                }
            }
            instance
        }
    }

}

/*object Aod {
    private var hasPatchedPanoramicAllDay = 0
    fun hookPanoramicAodAllDay(lpparam: XC_LoadPackage.LoadPackageParam) {
        val companionClass = XposedHelpers.findClass(
            "com.oplus.systemui.aod.display.SmoothTransitionController\$Companion",
            lpparam.classLoader
        )
        XposedHelpers.findAndHookMethod(
            companionClass,
            "getInstance",
            Context::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val instance = param.result
                    if (instance != null) {
                        try {
                            XposedHelpers.setBooleanField(
                                instance,
                                "isSupportPanoramicAllDay",
                                true
                            )
                            XposedHelpers.setBooleanField(
                                instance,
                                "isSupportPanoramicAllDayByPanelFeature",
                                true
                            )
                            if (hasPatchedPanoramicAllDay < 40) {
                                log(
                                    message = "✅ 成功启用 Panoramic All-Day AOD",
                                    tag = "PanoramicAllDay"
                                )
                                hasPatchedPanoramicAllDay++
                            }
                        } catch (e: Exception) {
                            log(
                                message = "❌ 修改字段失败" +
                                        "错误信息: ${e.message}," +
                                        "详情可在 com.gustate.mcga 中查看",
                                tag = "PanoramicAllDay"
                            )
                        }
                    }
                }
            }
        )
    }
}*/