package com.gustate.mcga.xposed.systemui.feature.qs.notification

import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getStaticField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.MethodHelper.proceedWithParam
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class NotificationHook {

    companion object {
        const val NOTIFICATION_LOG = "通知中心通知"
    }

    /**
     * 修改通知中心通知背景圆角半径
     * 适配 ColorOS V16.1.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadius 圆角半径
     */
    fun modifyNotificationCornerRadius(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadius: Float
    ) {
        val classLoader = param.classLoader
        try {

            // 取 RoundableState 类, 他是通知圆角的控制中枢
            val roundableStateClazz = loadClass(
                className = "com.android.systemui.statusbar.notification.RoundableState",
                classLoader = classLoader
            )

            /**
             * 取 RoundableState 类的构造函数与 setMaxRadius 函数
             *
             * 小剧场:
             * - Meng: 为什么呢牢汩? 你构造函数里只修改了传入的 maxRadius
             * - Meng: 你下面不是 hook maxRadius setter 函数了嘛
             * - Gu: 呵呵, 草特么傻逼 ColorOS, 有 setter 不用, 直接改字段是何意味
             *
             *  感谢 酷安大佬 [@tikaliu](https://www.coolapk.com/u/36684465)
             */
            // RoundableState(View view, final Roundable roundable, float f)
            // Roundable: com.android.systemui.statusbar.notification.Roundable
            val roundableStateConstructor = roundableStateClazz
                .getDeclaredConstructor(
                    View::class.java,
                    loadClass(
                        className = "com.android.systemui.statusbar.notification.Roundable",
                        classLoader = classLoader
                    ),
                    Float::class.javaPrimitiveType
                )
            // 修改任何视图构建 Roundable 之处传入的圆角半径
            module.hook(roundableStateConstructor).intercept { chain ->
                try {
                    chain.proceedWithParam(
                        index = 2,
                        value = cornerRadius
                    )
                } catch (e: Exception) {
                    log(
                        module = module, tag = NOTIFICATION_LOG,
                        message = "❌ 修改通知中心通知背景圆角半径失败",
                        throwable = e
                    )
                    chain.proceed()
                }
            }

            // void setMaxRadius(float f)
            val setMaxRadiusMethod = roundableStateClazz.getAnyMethod(
                methodName = "setMaxRadius",
                parameterTypes = arrayOf(
                    Float::class.javaPrimitiveType
                )
            )
            // 修改任何试图设置 Roundable 中 maxRadius 之处传入的圆角半径
            module.hook(setMaxRadiusMethod).intercept { chain ->
                try {
                    chain.proceedWithParam(
                        index = 0,
                        value = cornerRadius
                    )
                } catch (e: Exception) {
                    log(
                        module = module, tag = NOTIFICATION_LOG,
                        message = "❌ 修改通知中心通知背景圆角半径失败",
                        throwable = e
                    )
                    chain.proceed()
                }
            }

            log(
                module = module, tag = NOTIFICATION_LOG,
                message = "✅ 成功修改通知中心通知背景圆角半径为 $cornerRadius dp"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = NOTIFICATION_LOG,
                message = "❌ 修改通知中心通知背景圆角半径失败",
                throwable = e
            )
        }
    }

    /**
     * 添加通知中心轮廓高光
     * 适配 ColorOS V16.1.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param strokeWidth 轮廓高光粗细
     */
    fun addNotificationHighlight(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        strokeWidth: Int
    ) {
        val classLoader = param.classLoader
        try {

            // 获取相关类
            val notificationBackgroundViewExtImpClazz = loadClass(
                className = "com.oplus.systemui.statusbar.notification.row." +
                        "NotificationBackgroundViewExtImp",
                classLoader = classLoader
            )
            val calculateGradientStrokeToolsClazz = loadClass(
                className = "com.oplusos.systemui.common.util.CalculateGradientStrokeTools",
                classLoader = classLoader
            )
            // void updateRadius(float f1, float f2)
            val updateRadiusMethod = notificationBackgroundViewExtImpClazz
                .getAnyMethod(
                    methodName = "updateRadius",
                    parameterTypes = arrayOf(
                        Float::class.javaPrimitiveType,
                        Float::class.javaPrimitiveType
                    )
                )
            module.hook(updateRadiusMethod).intercept { chain ->
                val instance = chain.thisObject
                val result = chain.proceed()
                try {
                    val bgView = instance.callAnyMethod<View>(methodName = "getBgView")
                    // 必须先执行函数, 将带有圆角的 BlurConfig 放入 BlurProxy, 后面有用
                    val viewBlurProxy = runCatching {
                        instance.callAnyMethod<Any>(methodName = "getViewBlurProxy")
                    }.getOrNull() ?: return@intercept result
                    val blurConfig = runCatching {
                        viewBlurProxy
                            .callAnyMethod<Any>(methodName = "getBlurConfig")
                    }.getOrNull() ?: return@intercept result
                    val calculateGradientStrokeToolsInstance = calculateGradientStrokeToolsClazz
                        .getStaticField<Any>(fieldName = "INSTANCE")
                    val strokeParams = runCatching {
                        calculateGradientStrokeToolsInstance.callAnyMethod<Any>(
                            methodName = "getGradientStrokeParams",
                            paramTypes = arrayOf(
                                Int::class.javaPrimitiveType,
                                Int::class.javaPrimitiveType,
                                Float::class.javaPrimitiveType,
                                Boolean::class.javaPrimitiveType
                            ),
                            bgView
                                .callAnyMethod<Int>(methodName = "getActualWidth"),
                            instance  // clipBottom
                                .callAnyMethod<Int>(methodName = "getClipBottom"),
                            chain.args[0],
                            false
                        )
                    }.getOrNull() ?: return@intercept result
                    strokeParams.callAnyMethod<Unit>(
                        methodName = "setWidth",
                        paramTypes = arrayOf(Int::class.javaPrimitiveType),
                        strokeWidth
                    )
                    // 修改高光粗细
                    blurConfig.callAnyMethod<Any>(
                        methodName = "setGradientStrokeLineParam",
                        paramTypes = arrayOf(
                            loadClass(
                                className = "com.oplus.posteffect.GradientStrokeLineParams",
                                classLoader = classLoader
                            )
                        ),
                        strokeParams
                    )
                    blurConfig.callAnyMethod<Any>(
                        methodName = "setEnableStaticBlurCorner",
                        paramTypes = arrayOf(Boolean::class.javaPrimitiveType),
                        true
                    )
                    viewBlurProxy.callAnyMethod<Any>(methodName = "applyBlurConfig")
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = module, tag = NOTIFICATION_LOG,
                        message = "❌ 添加通知高光失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }

            log(
                module = module, tag = NOTIFICATION_LOG,
                message = "✅ 成功添加为 $strokeWidth dp 的通知中心轮廓高光"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = NOTIFICATION_LOG,
                message = "❌ 添加通知高光失败",
                throwable = e
            )
        }
    }
}