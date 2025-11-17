package com.gustate.mcga.xposed.systemui.feature

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object QsDetail {

    fun hookQsDetailContainer(
        lpparam: XC_LoadPackage.LoadPackageParam,
        blurRadius: Int,
        cornerRadius: Float,
        backgroundColor: Int
    ) {

        // 哪个傻蛋把模糊写进 ViewModel 里了......
        val viewModelClass = XposedHelpers.findClass(
            "com.oplus.systemui.qs.detail.viewmodel.QSDetailContainerViewModel",
            lpparam.classLoader
        )

        // hook getBackground() 函数
        XposedBridge.hookMethod(
            viewModelClass.getDeclaredMethod(
                "getBackground",
                View::class.java
            ),
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    try {

                        // 拿出来~ 啊~ 💗 好爽~ 要设在里面了... 啊~
                        val view = param.args[0] as View
                        val context = view.context

                        // 处理 dp 值
                        val cornerRadiusPx = cornerRadius.dpToPx(context)

                        // fallback 背景（模糊未生效时显示）
                        val fallback = GradientDrawable().apply {
                            setColor(backgroundColor)
                            setCornerRadius(cornerRadiusPx)
                        }

                        // 创建自定义模糊 Drawable
                        val customBlur = createCustomBlurDrawable(
                            view = view,
                            fallback = fallback,
                            classLoader = lpparam.classLoader,
                            blurRadius = blurRadius,
                            cornerRadiusPx = cornerRadiusPx,
                            backgroundColor = backgroundColor
                        )

                        param.result = customBlur
                        log(
                            message = "✅ QsDetail 容器自定义生效, " +
                                    "圆角 = $cornerRadius dp, 模糊=$blurRadius",
                            tag = "QsDetail"
                        )

                    } catch (e: Throwable) {
                        log(
                            message = "❌ QsDetail 容器自定义失败" +
                                    "错误信息: ${e.message}," +
                                    "详情可在 com.gustate.mcga 中查看",
                            tag = "QsDetail"
                        )
                        throw e
                    }
                }
            }
        )
    }

    private fun createCustomBlurDrawable(
        view: View,
        fallback: Drawable,
        classLoader: ClassLoader,
        blurRadius: Int,
        cornerRadiusPx: Float,
        backgroundColor: Int
    ): Drawable {

        // 构造 BlurConfig
        val blurConfigClass = classLoader.loadClass("com.oplusos.systemui." +
                "common.blurability.BlurConfig")
        val noneClass = classLoader.loadClass("com.oplusos.systemui." +
                "common.blurability.BlurMixConfig\$None")
        val noneInstance = XposedHelpers.getStaticObjectField(noneClass,
            "INSTANCE")
        // 使用默认构造（参数全默认）
        val blurConfig = XposedHelpers.newInstance(
            blurConfigClass,
            blurRadius,                     // blurRadius
            backgroundColor,                        // blurColor
            null,                                   // radiusWeight (Float)
            false,                                  // enableStaticBlurCorner
            noneInstance,                           // 背景混色 (禁用)
            null,                                   // motionBlurMixConfig
            null,                                   // windowBlurConfig
            0x1FFF                                  // mask: 所有参数使用默认值
        )

        // 构造 MixColor
        val mixColorClass = classLoader
            .loadClass("com.oplusos.systemui.common.blurability.MixColor")
        val foregroundMix = XposedHelpers
            .newInstance(mixColorClass, 3, 0, backgroundColor)
        val backgroundMix = XposedHelpers
            .newInstance(mixColorClass, 5, 0, backgroundColor)

        // 构造 BlurMixMulti
        val blurMixMultiClass = classLoader.loadClass(
            "com.oplusos.systemui.common.blurability.BlurMixConfig\$BlurMixMulti"
        )
        val blurMixMulti = XposedHelpers.newInstance(
            blurMixMultiClass, foregroundMix, backgroundMix)

        // 设置关键参数
        XposedHelpers.callMethod(blurConfig,
            "setBlurRadius", blurRadius)
        XposedHelpers.callMethod(blurConfig,
            "setCornerRadius", cornerRadiusPx)
        XposedHelpers.callMethod(blurConfig,
            "setPlatformMixConfig", blurMixMulti)
        XposedHelpers.callMethod(blurConfig,
            "setRadiusWeight", 1.0f)
        XposedHelpers.callMethod(blurConfig,
            "setEnableStaticBlurCorner", true)

        // ViewBlurProxy
        val viewBlurProxyClass = classLoader.loadClass("com.oplusos.systemui." +
                "common.blurability.ViewBlurProxy")
        val viewBlurProxy = XposedHelpers.newInstance(
            viewBlurProxyClass,
            view,            // View
            blurConfig,              // BlurConfig
            null,                    // StaticBlurManager (可为 null)
            null,                    // Function1 excludeRules (可为 null)
            null                     // BlurConfigsProvider (可为 null)
        )

        // 设置模糊类型为 PlatformStatic
        val blurTypeClass = classLoader
            .loadClass("com.oplusos.systemui.common.blurability." +
                    "ViewBlurProxy\$BlurType\$BlurTypePlatformStatic")
        val blurTypeInstance = XposedHelpers
            .getStaticObjectField(blurTypeClass, "INSTANCE")
        XposedHelpers
            .callMethod(viewBlurProxy, "setBlurType", blurTypeInstance)

        // === 5. 创建 AutoBlurDrawable ===
        val autoBlurClass = classLoader
            .loadClass("com.oplusos.systemui.common.blurability.drawable.AutoBlurDrawable")
        return XposedHelpers.newInstance(autoBlurClass, viewBlurProxy, fallback) as Drawable

    }
}