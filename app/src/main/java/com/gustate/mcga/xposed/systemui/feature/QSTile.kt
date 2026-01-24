package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object QSTile {

    fun hookQsOneXOneTile(
        lpparam: XC_LoadPackage.LoadPackageParam,
        bkgCornerRadius: Float
    ) {
        val logTag = "QsTile 1x1"
        try {
            // OplusQSResizeableTileViewOneXOne
            val tileViewClass = XposedHelpers.findClass(
                "com.oplus.systemui.plugins." +
                        "qs.customize.view.tile.OplusQSResizeableTileViewOneXOne",
                lpparam.classLoader
            )
            // Hook getViewRadius() 返回圆角（px）
            XposedBridge.hookMethod(
                tileViewClass.getDeclaredMethod("getViewRadius"),
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        val view = param.thisObject
                        val context = XposedHelpers
                            .getObjectField(view, "mContext") as? Context
                            ?: return param
                        val radiusPx = bkgCornerRadius.dpToPx(context)
                        return radiusPx
                    }
                }
            )
            // Hook createBgOutlineProvider 禁用 circleShape
            XposedBridge.hookMethod(
                tileViewClass.getDeclaredMethod(
                    "createBgOutlineProvider",
                    View::class.java
                ),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val outlineProvider = param.result
                        // 调用 setCircleShape(false)
                        try {
                            val setCircleShapeMethod = outlineProvider.javaClass.getMethod(
                                "setCircleShape",
                                Boolean::class.java
                            )
                            setCircleShapeMethod.invoke(outlineProvider, false)
                        } catch (_: Exception) {
                        }
                    }
                }
            )
            log(
                message = "✅ QsTile1x1 已修改为半径为 " +
                        "$bkgCornerRadius dp 的圆角矩形",
                tag = logTag
            )
        } catch (e: Exception) {
            log(
                message = "❌ 修改 QsTile 1x1 失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看",
                tag = logTag
            )
            throw e
        }
    }

    fun hookQSResizeableTile(
        lpparam: XC_LoadPackage.LoadPackageParam,
        bkgCornerRadius: Float
    ) {
        try {
            val clazz = XposedHelpers.findClass(
                "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSResizeableTileView",
                lpparam.classLoader
            )
            val oplusQSIconViewClass = XposedHelpers.findClass(
                "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
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
                            bkgCornerRadius.dpToPx(context)
                        } else NullPointerException("Context is Null")
                        // 定义圆角半径，单位为 px
                        return cornerRadiusDp
                    }
                }
            )
            log(
                message = "✅ 成功修改 QSResizeableTile" +
                        "整体背景圆角半径：$bkgCornerRadius",
                tag = "QSResizeableTile"
            )
        } catch (e: Throwable) {
            log(
                message = "❌ 修改 QSResizeableTile 失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看",
                tag = "QSResizeableTile"
            )
            throw e
        }
    }
}