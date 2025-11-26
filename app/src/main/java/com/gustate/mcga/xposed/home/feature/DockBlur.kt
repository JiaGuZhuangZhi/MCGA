package com.gustate.mcga.xposed.home.feature

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DockBlur {

    fun enableDockBlur(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            // 控制 Dock 栏背景的 oplusHotseatClass 类
            val oplusHotseatClass = XposedHelpers.findClass(
                "com.android.launcher3.OplusHotseat",
                lpparam.classLoader
            )
            val screenUtilsClass = XposedHelpers.findClass(
                "com.android.common.util.ScreenUtils",
                lpparam.classLoader
            )
            // 修改 setDockerBackground 强制启用模糊
            XposedBridge.hookAllMethods(
                oplusHotseatClass,
                "setDockerBackground",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        val instance = param.thisObject
                        val mContext = XposedHelpers.getObjectField(instance, "mContext")
                        val mShortcutsAndWidgets =
                            XposedHelpers.getObjectField(instance, "mShortcutsAndWidgets")

                        val childCount =
                            XposedHelpers.callMethod(mShortcutsAndWidgets, "getChildCount") as Int
                        if (childCount == 0) {
                            XposedHelpers.callMethod(
                                mShortcutsAndWidgets,
                                "setBackgroundResource",
                                0
                            )
                            return null
                        }

                        // Force use createBlurDrawable if supported, ignore hasLargeDisplayFeatures
                        var drawable: Any? = null
                        try {
                            val isSupportNewBlur = XposedHelpers.callStaticMethod(
                                lpparam.classLoader.loadClass("com.android.launcher3.uioverrides.states.blurdrawable.OplusBlurProperties"),
                                "isSupportNewBlur",
                                mContext,
                                true
                            ) as Boolean

                            if (isSupportNewBlur) {
                                drawable = XposedHelpers.callMethod(instance, "createBlurDrawable")
                            }
                        } catch (e: Throwable) {
                            XposedBridge.log("DockBlur: createBlurDrawable failed: ${e.message}")
                        }

                        // fallback
                        if (drawable == null) {
                            drawable = XposedHelpers.callStaticMethod(
                                lpparam.classLoader.loadClass("com.android.launcher3.hotseat.expand.ExpandUtils"),
                                "getHotseatNormalBgDrawable",
                                mContext
                            )
                        }

                        if (drawable != null) {
                            XposedHelpers.callMethod(
                                mShortcutsAndWidgets,
                                "setBackground",
                                drawable
                            )
                        }

                        val mBackgroundVisible =
                            XposedHelpers.getBooleanField(instance, "mBackgroundVisible")
                        val alpha = if (mBackgroundVisible) 1.0f else 0.0f
                        XposedHelpers.callMethod(instance, "setBackgroundAlpha", alpha)

                        return null
                    }
                })

            // Step 2: Force call setDockerBackground in key lifecycle methods
            val methodsToHook =
                arrayOf("onMeasure", "onAttachedToWindow", "onWallpaperBrightnessChanged")
            for (methodName in methodsToHook) {
                XposedBridge.hookAllMethods(
                    oplusHotseatClass,
                    methodName,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            // Only trigger if child count > 0
                            val mShortcutsAndWidgets = XposedHelpers.getObjectField(
                                param.thisObject,
                                "mShortcutsAndWidgets"
                            )
                            val childCount = XposedHelpers.callMethod(
                                mShortcutsAndWidgets,
                                "getChildCount"
                            ) as Int
                            if (childCount > 0) {
                                try {
                                    XposedHelpers.callMethod(
                                        param.thisObject,
                                        "setDockerBackground"
                                    )
                                } catch (e: Throwable) {
                                    XposedBridge.log("DockBlur: Failed to call setDockerBackground in $methodName: ${e.message}")
                                }
                            }
                        }
                    })
            }

            XposedBridge.log("DockBlurHook: Successfully forced Dock blur on phone.")
        } catch (e: Throwable) {
            XposedBridge.log("DockBlurHook error: ${e.message}")
            e.printStackTrace()
        }
    }

}