package com.gustate.mcga.xposed.home.feature

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.util.WeakHashMap

class DockHook {

    companion object {
        const val DOCK_BKG = "Dock 栏背景"
    }

    // 是否正在创建 Dock 背景
    private var isCreatingDockBkg = false

    // 白名单 Properties 实例容器
    // 这里不用 isCreatingDockBkg 是因为 Properties 有大量协程
    private val dockPropertiesMap = WeakHashMap<Any, Boolean>()

    fun hookDock(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        enableDockBlur: Boolean,
        blurRadius: Int,
        cornerRadius: Float
    ) {
        // 获取 ClassLoader 实例
        val classLoader = param.classLoader
        // 换算单位
        val cornerRadiusPx = cornerRadius
            .dpToPx(ContextHelper.getContext(classLoader))

        // 加载所需类
        // 加载 Dock 类
        val dockClass = loadClass(
            className = "com.android.launcher3.OplusHotseat",
            classLoader = classLoader
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 OplusHotseat 类"
        )
        // 加载模糊工具类
        val blurUtils = loadClass(
            className = "com.oplus.utils.OplusBlurProperties",
            classLoader = classLoader
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 OplusBlurProperties 类"
        )
        // 加载屏幕工具类
        val screenUtils = loadClass(
            className = "com.oplus.utils.ScreenUtils",
            classLoader = classLoader
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 ScreenUtils 类"
        )

        // hook 设置 Dock 背景方法, 执行时标记
        val setDockBkgMethod = dockClass
            .getDeclaredMethod("setDockerBackground")
            ?: return log(
                module = module, tag = DOCK_BKG,
                message = "❌ 未获取到 setDockerBackground 函数"
            )
        module.hook(setDockBkgMethod)
            .intercept { chain ->
                // 标记一下, 正在创建背景
                isCreatingDockBkg = true
                // 执行原函数逻辑
                val result = chain.proceed()
                // 标记一下, 创建背景结束
                isCreatingDockBkg = false
                // 返回强制修改某些判断字段后的值
                result
            }

        // hook 创建模糊背景方法, 登记所属 Properties
        val createBlurMethod = dockClass
            .getDeclaredMethod("createBlurDrawable")
            ?: return log(
                module = module, tag = DOCK_BKG,
                message = "❌ 未获取到 createBlurDrawable 函数"
            )
        module.hook(createBlurMethod).intercept { chain ->
            // 执行原逻辑
            val result = chain.proceed()
            // 获取当前实例
            val instance = chain.thisObject
            // 从 Hotseat 实例里拿走它的 mBlurProp
            val mBlurProp = instance
                ?.getAnyField<Any>(fieldName = "mBlurProp")
                ?: run {
                    log(
                        module = module, tag = DOCK_BKG,
                        message = "❌ 未获取到 mBlurProp 字段 已使用默认 Dock 栏"
                    )
                    return@intercept result
                }
            // 登记：这个 Properties 对象是属于 Dock 的
            dockPropertiesMap[mBlurProp] = true
            // 直接返回原结果
            result
        }

        // 强制启用 Dock 栏背景
        enableDockBkgForce(
            module = module,
            enableDockBlur = enableDockBlur,
            screenUtils = screenUtils,
            blurUtils = blurUtils,
            dockClass = dockClass
        )
        modifyDockBkg(
            module = module,
            blurRadius = blurRadius,
            cornerRadiusPx = cornerRadiusPx,
            classLoader = classLoader,
            blurUtils = blurUtils,
        )

    }

    /**
     * 强制启用 Dock 栏背景
     * @param module 模块入口类
     * @param enableDockBlur 是否启用背景模糊
     * @param screenUtils 屏幕工具类
     * @param blurUtils 模糊工具类
     * @param dockClass Dock 栏类
     */
    private fun enableDockBkgForce(
        module: XposedModule,
        enableDockBlur: Boolean,
        screenUtils: Class<*>?,
        blurUtils: Class<*>?,
        dockClass: Class<*>?
    ) {

        // hook 是否为大屏判断之函数
        val hasLargeMethod = screenUtils
            ?.getDeclaredMethod("hasLargeDisplayFeatures")
            ?: return log(
                module = module, tag = DOCK_BKG,
                message = "❌ 未获取到 hasLargeDisplayFeatures 函数"
            )
        module.hook(hasLargeMethod).intercept { chain ->
            // 当被 setDockerBackground() 调用时强制返回 true
            if (isCreatingDockBkg) true
            // 其他情况直接执行原判断逻辑
            else chain.proceed()
        }

        // hook 是否支持模糊判断之函数
        val isSupportMethod = blurUtils?.getDeclaredMethod(
            "isSupportNewBlur",
            Context::class.java,
            Boolean::class.javaPrimitiveType
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 isSupportNewBlur 函数"
        )
        if (enableDockBlur) {
            module.hook(isSupportMethod).intercept { chain ->
                // 当被 setDockerBackground() 调用时强制返回 true
                if (isCreatingDockBkg) true
                // 其他情况直接执行原判断逻辑
                else chain.proceed()
            }
        }

        // 联防联控, 把所有重载方法都直接调用 setDockerBackground
        val measureMethods = arrayOf(
            dockClass?.getDeclaredMethod("onAttachedToWindow")
                ?: return log(
                    module = module, tag = DOCK_BKG,
                    message = "❌ 未获取到 onAttachedToWindow 函数"
                ),
            dockClass.getDeclaredMethod("onWallpaperBrightnessChanged")
                ?: return log(
                    module = module, tag = DOCK_BKG,
                    message = "❌ 未获取到 onWallpaperBrightnessChanged 函数"
                ),
            dockClass.getDeclaredMethod(
                "onMeasure",
                Int::class.java,
                Int::class.java
            ) ?: return log(
                module = module, tag = DOCK_BKG,
                message = "❌ 未获取到 onMeasure 函数"
            )
        )
        measureMethods.forEach { method ->
            module.hook(method).intercept { chain ->
                // 执行原逻辑
                val result = chain.proceed()
                // 获取当前实例
                val instance = chain.thisObject
                    ?: return@intercept result
                // 执行 setDockerBackground
                instance.callAnyMethod<Unit>(methodName = "setDockerBackground")
                log(
                    module = module, tag = DOCK_BKG,
                    message = "✅ 成功强制在重载时执行 setDockerBackground"
                )
                // 返回原值
                result
            }
        }
    }

    /**
     * 修改 Dock 栏背景
     * @param module 模块入口类
     * @param blurRadius 背景模糊半径
     * @param cornerRadiusPx 背景圆角半径 (像素)
     * @param classLoader [ClassLoader] 实例
     * @param blurUtils 模糊工具类
     */
    private fun modifyDockBkg(
        module: XposedModule,
        blurRadius: Int,
        cornerRadiusPx: Float,
        classLoader: ClassLoader,
        blurUtils: Class<*>?
    ) {

        // 这里对 BlurRadius 进行了傻逼般的硬编码
        // hook toUXRadius 方法取消硬编码
        val toUXRadius = blurUtils?.getDeclaredMethod(
            "toUXRadius",
            Int::class.javaPrimitiveType
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 toUXRadius 函数"
        )
        module.hook(toUXRadius).intercept { chain ->
            var result = chain.proceed() as? String ?: "800"
            // 获取当前 BlurParams 实例
            val currentProp = chain.thisObject ?: return@intercept chain.proceed()
            // 是 Dock 创建的实例进行参数修改
            if (dockPropertiesMap[currentProp] == true) {
                result = chain.args[0].toString()
                log(
                    module = module, tag = DOCK_BKG,
                    message = "✅ 成功取消 Dock 栏圆角半径硬编码"
                )
            }
            return@intercept result
        }

        // hook 模糊配置方法
        val setParamsMethod = blurUtils.getDeclaredMethod(
            "setBlurParams",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 setBlurParams 函数"
        )
        // 修改模糊半径
        module.hook(setParamsMethod).intercept { chain ->
            // 拷贝一份原参数使其可变
            val newArgs = chain.args.toMutableList()
            // 获取当前 BlurParams 实例
            val currentProp = chain.thisObject
                ?: return@intercept chain.proceed()
            // 是 Dock 创建的实例进行参数修改
            if (dockPropertiesMap[currentProp] == true) {
                newArgs[0] = blurRadius
                log(
                    module = module, tag = DOCK_BKG,
                    message = "✅ 成功修改 Dock 栏模糊半径为 $blurRadius"
                )
            }
            // 将修改后的参数列表传给 proceed
            chain.proceed(newArgs.toTypedArray())
        }

        // hook 圆角配置方法
        val setCornerRadiusMethod = blurUtils.getDeclaredMethod(
            "setBlurCornerRadius",
            Float::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType,
            loadClass(
                className = "com.android.launcher3.model.data.ItemInfo",
                classLoader = classLoader
            ),
        ) ?: return log(
            module = module, tag = DOCK_BKG,
            message = "❌ 未获取到 setBlurCornerRadius 函数或其参数"
        )
        module.hook(setCornerRadiusMethod).intercept { chain ->
            // 拷贝一份原参数使其可变
            val newArgs = chain.args.toMutableList()
            // 获取当前 BlurParams 实例
            val currentProp = chain.thisObject
                ?: return@intercept chain.proceed()
            // 是 Dock 创建的实例进行参数修改
            if (dockPropertiesMap[currentProp] == true) {
                newArgs[0] = cornerRadiusPx
                log(
                    module = module, tag = DOCK_BKG,
                    message = "✅ 成功修改 Dock 栏圆角半径为 $cornerRadiusPx"
                )
            }
            // 将修改后的参数列表传给 proceed
            chain.proceed(newArgs.toTypedArray())
        }
    }
}