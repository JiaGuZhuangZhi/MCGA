package com.gustate.mcga.xposed.systemui.feature

import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object QSTile {
    fun hookQSResizeableTile(
        lpparam: XC_LoadPackage.LoadPackageParam,
        cornerRadius: Float,
    ) {
        try {
            val clazz = XposedHelpers.findClass(
                "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSResizeableTileView",
                lpparam.classLoader
            )
            XposedBridge.hookMethod(
                clazz.getDeclaredMethod("getRadius"),
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(
                        param: MethodHookParam
                    ): Any {
                        val view = param.thisObject as? View
                        val context = view?.context
                        val cornerRadiusDp = if (context != null) {
                            cornerRadius.dpToPx(context)
                        } else NullPointerException("Context is Null")
                        // 定义圆角半径，单位为 px
                        return cornerRadiusDp
                    }
                }
            )
            log(
                message = "✅ 成功修改 QSResizeableTile" +
                        "圆角半径：$cornerRadius",
                tag = "QSResizeableTile"
            )
        } catch (e: Throwable) {
            log(
                message = "❌ 修改 QSResizeableTile 失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看",
                tag = "QSResizeableTile"
            )
        }
    }
}