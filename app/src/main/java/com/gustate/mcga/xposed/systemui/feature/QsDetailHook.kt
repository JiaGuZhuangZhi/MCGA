package com.gustate.mcga.xposed.systemui.feature

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.getStaticField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ClassHelper.setAnyField
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class QsDetailHook {

    companion object {
        const val QS_DETAIL_CONTAINER_LOG = "快速设置面板背景"
    }

    /**
     * Hook QsDetail 容器背景
     * 别问为什么，问就是那个傻蛋把模糊写进 ViewModel 里了......
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param blurRadius 模糊半径
     * @param cornerRadius 圆角大小 (dp)
     * @param foregroundColor 前景混色
     * @param backgroundColor 背景色
     */
    fun hookQsDetailContainer(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        blurRadius: Int,
        cornerRadius: Float,
        foregroundColor: Int,
        backgroundColor: Int
    ) {
        val viewModelClass = loadClass(
            "com.oplus.systemui.qs.detail.viewmodel.QSDetailContainerViewModel",
            param.classLoader
        ) ?: return

        try {
            // 获取 getBackground(View) 方法
            val getBackgroundMethod =
                viewModelClass.getDeclaredMethod("getBackground", View::class.java)

            // 开始拦截，准备把自定义的东西塞进去
            module.hook(getBackgroundMethod).intercept { chain ->
                // 执行原方法拿到 View 参数
                val view = chain.args[0] as View
                val context = view.context

                // 处理 dp 值
                val cornerRadiusPx = cornerRadius.dpToPx(context)

                // fallback 背景（模糊未生效时显示）
                val fallback = GradientDrawable().apply {
                    setColor(backgroundColor)
                    setCornerRadius(cornerRadiusPx)
                }

                // 拿出来~ 啊~ 💗 好爽~ 要设在里面了... 啊~
                val customBlur = createCustomBlurDrawable(
                    view = view,
                    fallback = fallback,
                    classLoader = param.classLoader,
                    blurRadius = blurRadius,
                    cornerRadiusPx = cornerRadiusPx,
                    foregroundColor = foregroundColor,
                    backgroundColor = backgroundColor
                )

                log(
                    module = module, tag = QS_DETAIL_CONTAINER_LOG,
                    message = "✅ QsDetail 容器自定义生效, 圆角 = $cornerRadius dp, 模糊 = $blurRadius"
                )
                customBlur // 顺滑地射出结果
            }
        } catch (e: Throwable) {
            log(
                module = module, tag = QS_DETAIL_CONTAINER_LOG,
                message = "❌ QsDetail 容器自定义失败: ${e.message}"
            )
        }
    }

    private fun createCustomBlurDrawable(
        view: View,
        fallback: Drawable,
        classLoader: ClassLoader,
        blurRadius: Int,
        cornerRadiusPx: Float,
        foregroundColor: Int,
        backgroundColor: Int
    ): Drawable {
        // 1. 构造 BlurConfig
        val blurConfigClass =
            classLoader.loadClass("com.oplusos.systemui.common.blurability.BlurConfig")
        val noneClass =
            classLoader.loadClass("com.oplusos.systemui.common.blurability.BlurMixConfig\$None")
        val noneInstance = noneClass.getStaticField<Any>("INSTANCE")

        // 构造函数反射 (这里由于参数较多且含基本类型，直接用原生反射构造)
        val blurConfig = blurConfigClass.getConstructor(
            Int::class.javaPrimitiveType, // blurRadius
            Int::class.javaPrimitiveType, // blurColor
            java.lang.Float::class.java,  // radiusWeight
            Boolean::class.javaPrimitiveType, // enableStaticBlurCorner
            classLoader.loadClass(
                "com.oplusos.systemui.common.blurability.BlurMixConfig"
            ), // backgroundMixConfig
            classLoader.loadClass(
                "com.oplusos.systemui.common.blurability.BlurMixConfig\$MotionBlurMixConfig"
            ),
            classLoader.loadClass(
                "com.oplusos.systemui.common.blurability.BlurConfig\$WindowBlurConfig"
            ),
            Int::class.javaPrimitiveType // mask
        ).newInstance(
            blurRadius,
            backgroundColor,
            null,
            false,
            noneInstance,
            null,
            null,
            0x1FFF
        )

        // 2. 构造 MixColor (前景 3, 背景 5)
        val mixColorClass = classLoader.loadClass(
            "com.oplusos.systemui.common.blurability.MixColor"
        )
        val mixConstructor = mixColorClass.getConstructor(
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )
        val foregroundMix = mixConstructor.newInstance(3, 0, foregroundColor)
        val backgroundMix = mixConstructor.newInstance(5, 0, backgroundColor)

        // 3. 构造 BlurMixMulti
        val blurMixMultiClass = classLoader.loadClass(
            "com.oplusos.systemui.common.blurability.BlurMixConfig\$BlurMixMulti"
        )
        val blurMixMulti = blurMixMultiClass
            .getConstructor(mixColorClass, mixColorClass)
            .newInstance(foregroundMix, backgroundMix)

        // 使用你的 ClassHelper.setAnyField 暴力注入
        blurConfig.setAnyField("blurRadius", blurRadius)
        blurConfig.setAnyField("cornerRadius", cornerRadiusPx)
        blurConfig.setAnyField("platformMixConfig", blurMixMulti)
        blurConfig.setAnyField("radiusWeight", 1.0f)
        blurConfig.setAnyField("enableStaticBlurCorner", true)

        // 4. ViewBlurProxy
        val viewBlurProxyClass = classLoader.loadClass(
            "com.oplusos.systemui.common.blurability.ViewBlurProxy"
        )
        val proxyConstructor = viewBlurProxyClass.constructors.first() // 拿第一个匹配的构造函数
        val viewBlurProxy = proxyConstructor.newInstance(
            view,
            blurConfig,
            null,
            null,
            null
        )

        // 设置模糊类型为 PlatformStatic
        val blurTypeClass = classLoader.loadClass(
            "com.oplusos.systemui.common.blurability.ViewBlurProxy\$BlurType" +
                    "\$BlurTypePlatformStatic"
        )
        val blurTypeInstance = blurTypeClass.getStaticField<Any>("INSTANCE")
        viewBlurProxy.setAnyField("blurType", blurTypeInstance)

        // 5. 创建 AutoBlurDrawable 并返回
        val autoBlurClass = classLoader.loadClass(
            "com.oplusos.systemui.common." +
                    "blurability.drawable.AutoBlurDrawable"
        )
        return autoBlurClass.getConstructor(
            viewBlurProxyClass,
            Drawable::class.java
        )
            .newInstance(viewBlurProxy, fallback) as Drawable
    }
}

/*package com.gustate.mcga.xposed.systemui.feature

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
        foregroundColor: Int,
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
                            foregroundColor = foregroundColor,
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
        foregroundColor: Int,
        backgroundColor: Int
    ): Drawable {

        // 构造 BlurConfig
        val blurConfigClass = classLoader.loadClass(
            "com.oplusos.systemui." +
                    "common.blurability.BlurConfig"
        )
        val noneClass = classLoader.loadClass(
            "com.oplusos.systemui." +
                    "common.blurability.BlurMixConfig\$None"
        )
        val noneInstance = XposedHelpers.getStaticObjectField(
            noneClass,
            "INSTANCE"
        )
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
            .newInstance(mixColorClass, 3, 0, foregroundColor)
        val backgroundMix = XposedHelpers
            .newInstance(mixColorClass, 5, 0, backgroundColor)

        // 构造 BlurMixMulti
        val blurMixMultiClass = classLoader.loadClass(
            "com.oplusos.systemui.common.blurability.BlurMixConfig\$BlurMixMulti"
        )
        val blurMixMulti = XposedHelpers.newInstance(
            blurMixMultiClass, foregroundMix, backgroundMix
        )

        // 设置关键参数
        XposedHelpers.callMethod(
            blurConfig,
            "setBlurRadius", blurRadius
        )
        XposedHelpers.callMethod(
            blurConfig,
            "setCornerRadius", cornerRadiusPx
        )
        XposedHelpers.callMethod(
            blurConfig,
            "setPlatformMixConfig", blurMixMulti
        )
        XposedHelpers.callMethod(
            blurConfig,
            "setRadiusWeight", 1.0f
        )
        XposedHelpers.callMethod(
            blurConfig,
            "setEnableStaticBlurCorner", true
        )

        // ViewBlurProxy
        val viewBlurProxyClass = classLoader.loadClass(
            "com.oplusos.systemui." +
                    "common.blurability.ViewBlurProxy"
        )
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
            .loadClass(
                "com.oplusos.systemui.common.blurability." +
                        "ViewBlurProxy\$BlurType\$BlurTypePlatformStatic"
            )
        val blurTypeInstance = XposedHelpers
            .getStaticObjectField(blurTypeClass, "INSTANCE")
        XposedHelpers
            .callMethod(viewBlurProxy, "setBlurType", blurTypeInstance)

        // === 5. 创建 AutoBlurDrawable ===
        val autoBlurClass = classLoader
            .loadClass("com.oplusos.systemui.common.blurability.drawable.AutoBlurDrawable")
        return XposedHelpers.newInstance(autoBlurClass, viewBlurProxy, fallback) as Drawable

    }

}*/