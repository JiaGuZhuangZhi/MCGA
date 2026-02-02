package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object Aod {
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
}