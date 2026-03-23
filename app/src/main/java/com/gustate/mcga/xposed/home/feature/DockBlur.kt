package com.gustate.mcga.xposed.home.feature

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DockBlur {

    private const val FIELD_DECORATOR = "mcga_hotseat_decorator"

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
            // 内部类的表示方式通常是 Outer$Inner
            val borderViewClass = XposedHelpers.findClass(
                "com.android.launcher.pageindicators.decorate.CustomBorderViewDecorator\$OpluIndicatorBorderView",
                lpparam.classLoader
            )

            XposedBridge.hookAllConstructors(oplusHotseatClass, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val hotseat = param.thisObject as android.view.ViewGroup
                    val context = hotseat.context

                    // 1. 实例化那个“高光边框”View
                    // 注意：根据源码，这个内部类的构造函数第一个参数是外部类实例
                    // 如果是静态内部类则不需要，但根据 Metadata 它似乎是非静态的
                    val decoratorInstance = XposedHelpers.newInstance(
                        XposedHelpers.findClass(
                            "com.android.launcher.pageindicators.decorate.CustomBorderViewDecorator",
                            lpparam.classLoader
                        )
                    )
                    val borderView = XposedHelpers.newInstance(
                        borderViewClass,
                        decoratorInstance,
                        context
                    ) as android.view.View

                    // 2. 将其添加到 Hotseat 中
                    hotseat.clipChildren = false
                    hotseat.clipToPadding = false
                    hotseat.addView(borderView)

                    // 存储引用以便后续操作
                    XposedHelpers.setAdditionalInstanceField(
                        hotseat,
                        "mcga_border_view",
                        borderView
                    )
                }
            })

            // 3. 关键：同步位置和大小
            XposedHelpers.findAndHookMethod(
                oplusHotseatClass,
                "onLayout",
                Boolean::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val borderView = XposedHelpers.getAdditionalInstanceField(
                            param.thisObject,
                            "mcga_border_view"
                        ) as? android.view.View
                        if (borderView != null) {
                            val l = param.args[1] as Int
                            val t = param.args[2] as Int
                            val r = param.args[3] as Int
                            val b = param.args[4] as Int
                            val width = r - l
                            val height = b - t

                            // 让 borderView 铺满整个 Hotseat
                            borderView.layout(0, 0, width, height)

                            // 调用源码中的 updateRect 来更新内部的 Path
                            XposedHelpers.callMethod(borderView, "updateRect", 0, 0, width, height)
                        }
                    }
                })

            // 4. 亮度感知同步
            XposedBridge.hookAllMethods(
                oplusHotseatClass,
                "onWallpaperBrightnessChanged",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val borderView = XposedHelpers.getAdditionalInstanceField(
                            param.thisObject,
                            "mcga_border_view"
                        )
                        if (borderView != null) {
                            XposedHelpers.callMethod(
                                borderView,
                                "onBrightnessChange",
                                param.args[0]
                            )
                        }
                    }
                })

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

                        val borderView = XposedHelpers.getAdditionalInstanceField(
                            param.thisObject,
                            "mcga_border_view"
                        )
                        XposedHelpers.callMethod(borderView, "updateHightLightAlpha", alpha)

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
            XposedBridge.log("DockBlurHook 发生错误: ${e.message}")
            e.printStackTrace()
        }
    }

}