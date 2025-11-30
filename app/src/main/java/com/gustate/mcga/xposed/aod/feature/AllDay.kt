package com.gustate.mcga.xposed.aod.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AllDay {
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
            throw e
        }
    }
}