package com.gustate.mcga.xposed.systemui.feature.qs.notification

import android.view.View
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.callStaticMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getStaticField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ClassHelper.setAnyField
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
                name = "com.android.systemui.statusbar.notification.RoundableState",
                loader = classLoader
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
                        name = "com.android.systemui.statusbar.notification.Roundable",
                        loader = classLoader
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
                name = "setMaxRadius",
                paramTypes = arrayOf(
                    Float::class.javaPrimitiveType
                ),
                classLoader = classLoader
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
                name = "com.oplus.systemui.statusbar.notification.row." +
                        "NotificationBackgroundViewExtImp",
                loader = classLoader
            )
            // void updateRadius(float f1, float f2)
            val updateRadiusMethod = notificationBackgroundViewExtImpClazz
                .getAnyMethod(
                    name = "updateRadius",
                    paramTypes = arrayOf(
                        Float::class.javaPrimitiveType,
                        Float::class.javaPrimitiveType
                    ),
                    classLoader = classLoader
                )
            module.hook(updateRadiusMethod).intercept { chain ->
                val instance = chain.thisObject
                val result = chain.proceed()
                try {
                    // OS 16.1 前期版本
                    val calculateGradientStrokeToolsClazz = loadClass(
                        name = "com.oplusos.systemui.common.util.CalculateGradientStrokeTools",
                        loader = classLoader
                    )
                    val bgView = instance.callAnyMethod<View>(
                        name = "getBgView",
                        classLoader = classLoader
                    )
                    // 必须先执行函数, 将带有圆角的 BlurConfig 放入 BlurProxy, 后面有用
                    val viewBlurProxy = runCatching {
                        instance.callAnyMethod<Any>(
                            name = "getViewBlurProxy",
                            classLoader = classLoader
                        )
                    }.getOrNull() ?: return@intercept result
                    val blurConfig = runCatching {
                        viewBlurProxy.callAnyMethod<Any>(
                            name = "getBlurConfig",
                            classLoader = classLoader
                        )
                    }.getOrNull() ?: return@intercept result
                    val calculateGradientStrokeToolsInstance = calculateGradientStrokeToolsClazz
                        .getStaticField<Any>(name = "INSTANCE")
                    val strokeParams = runCatching {
                        calculateGradientStrokeToolsInstance.callAnyMethod<Any>(
                            name = "getGradientStrokeParams",
                            params = arrayOf(
                                bgView.callAnyMethod<Int>(
                                    name = "getActualWidth",
                                    classLoader = classLoader
                                ),
                                instance.callAnyMethod<Int>(
                                    name = "getClipBottom",
                                    classLoader = classLoader
                                ),
                                chain.args[0],
                                false
                            ),
                            paramTypes = arrayOf(
                                Int::class.javaPrimitiveType,
                                Int::class.javaPrimitiveType,
                                Float::class.javaPrimitiveType,
                                Boolean::class.javaPrimitiveType
                            ),
                            classLoader = classLoader,
                        )
                    }.getOrNull() ?: return@intercept result
                    strokeParams.callAnyMethod<Unit>(
                        name = "setWidth",
                        params = arrayOf(strokeWidth),
                        paramTypes = arrayOf(Int::class.javaPrimitiveType),
                        classLoader = classLoader
                    )
                    // 修改高光粗细
                    blurConfig.callAnyMethod<Any>(
                        name = "setGradientStrokeLineParam",
                        params = arrayOf(strokeParams),
                        paramTypes = arrayOf(
                            loadClass(
                                name = "com.oplus.posteffect.GradientStrokeLineParams",
                                loader = classLoader
                            )
                        ),
                        classLoader = classLoader
                    )
                    blurConfig.callAnyMethod<Any>(
                        name = "setEnableStaticBlurCorner",
                        params = arrayOf(true),
                        paramTypes = arrayOf(Boolean::class.javaPrimitiveType),
                        classLoader = classLoader
                    )
                    viewBlurProxy.callAnyMethod<Any>(
                        name = "applyBlurConfig",
                        classLoader = classLoader
                    )
                    return@intercept result
                } catch (_: NullPointerException) {
                    // OS 16.1 后期版本
                    val gradientStrokeLineAdapterClazz = loadClass(
                        name = "com.oplusos.systemui.common.util.GradientStrokeLineAdapter",
                        loader = classLoader
                    )
                    val bgView = instance.callAnyMethod<View>(
                        name = "getBgView",
                        classLoader = classLoader
                    )
                    // 必须先执行函数, 将带有圆角的 BlurConfig 放入 BlurProxy, 后面有用
                    val viewBlurProxy = runCatching {
                        instance.callAnyMethod<Any>(
                            name = "getViewBlurProxy",
                            classLoader = classLoader
                        )
                    }.getOrNull() ?: return@intercept result
                    val blurConfig = runCatching {
                        viewBlurProxy.callAnyMethod<Any>(
                            name = "getBlurConfig",
                            classLoader = classLoader
                        )
                    }.getOrNull() ?: return@intercept result
                    val gradientStrokeLineAdapterInstance = gradientStrokeLineAdapterClazz
                        .getStaticField<Any>(name = "INSTANCE")
                    val strokeParamsTemplate = gradientStrokeLineAdapterClazz
                        .callStaticMethod<Any>(
                            name = "getNotificationStrokeParamsTemplate",
                            params = arrayOf(true),
                            paramTypes = arrayOf(Boolean::class.javaPrimitiveType),
                            classLoader = classLoader
                        )
                    strokeParamsTemplate.setAnyField(
                        name = "lineWidth",
                        value = strokeWidth
                    )
                    gradientStrokeLineAdapterInstance.callAnyMethod<Any>(
                        name = "adaptGradientStrokeParams",
                        params = arrayOf(
                            blurConfig,
                            blurConfig.callAnyMethod<Any>(
                                name = "getCornerRadius",
                                classLoader = classLoader
                            ),
                            blurConfig.callAnyMethod<Any>(
                                name = "getRadiusWeight",
                                classLoader = classLoader
                            ),
                            bgView.callAnyMethod<Int>(
                                name = "getActualWidth",
                                classLoader = classLoader
                            ),
                            instance.callAnyMethod<Int>(
                                name = "getClipBottom",
                                classLoader = classLoader
                            ),
                            true,
                            strokeParamsTemplate
                        ),
                        paramTypes = arrayOf(
                            loadClass(
                                name = "com.oplusos.systemui.common.blurability.BlurConfig",
                                loader = classLoader
                            ),
                            Float::class.javaPrimitiveType,
                            java.lang.Float::class.java,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                            Boolean::class.javaPrimitiveType,
                            loadClass(
                                name = "com.oplusos.systemui.common.util." +
                                        $$"GradientStrokeLineAdapter$StrokeParamsTemplate",
                                loader = classLoader
                            )
                        ),
                        classLoader = classLoader
                    )
                    val strokeParams = runCatching {
                        blurConfig.callAnyMethod<Any>(
                            name = "getGradientStrokeLineParam",
                            classLoader = classLoader
                        )
                    }.getOrNull() ?: return@intercept result
                    strokeParams.callAnyMethod<Unit>(
                        name = "setWidth",
                        params = arrayOf(strokeWidth),
                        paramTypes = arrayOf(Int::class.javaPrimitiveType),
                        classLoader = classLoader
                    )
                    // 修改高光粗细
                    blurConfig.callAnyMethod<Any>(
                        name = "setGradientStrokeLineParam",
                        params = arrayOf(strokeParams),
                        paramTypes = arrayOf(
                            loadClass(
                                name = "com.oplus.posteffect.GradientStrokeLineParams",
                                loader = classLoader
                            )
                        ),
                        classLoader = classLoader
                    )
                    viewBlurProxy.callAnyMethod<Any>(
                        name = "applyBlurConfig",
                        classLoader = classLoader
                    )
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