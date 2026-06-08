package com.gustate.mcga.xposed.systemui.feature.qs.tile

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_MEDIA_LOG
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 控制中心媒体磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class MediaTileHook {

    /**
     * 修改控制中心媒体磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun modifyCornerRadius(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        val osVer = RootUtils.getColorOSVersion()
        if (osVer.startsWith(prefix = "V16.1"))
            modifyCornerRadiusOS161(
                module = module,
                param = param,
                cornerRadiusDp = cornerRadiusDp
            )
        else
            modifyCornerRadiusOS160(
                module = module,
                param = param,
                cornerRadiusDp = cornerRadiusDp
            )
    }

    /**
     * 修改控制中心媒体磁贴圆角半径
     * 适配 ColorOS V16.1.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    private fun modifyCornerRadiusOS161(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        val classLoader = param.classLoader
        try {
            val sepQSResPoolClazz = loadClass(
                className = "com.oplus.systemui.qs.base.res.SepQSResPool",
                classLoader = classLoader
            )
            val qsConstantClazz = loadClass(
                className = "com.oplus.systemui.qs.base.res.util.QSConstant",
                classLoader = classLoader
            )
            val getMediaPanelOutline = sepQSResPoolClazz
                .getDeclaredMethod("getMediaPanelOutline")
            val getCustomOutline = qsConstantClazz.getDeclaredMethod(
                "getSmoothRoundRectOutlineProvider",
                Context::class.java,
                Float::class.javaPrimitiveType
            )
            module.hook(getMediaPanelOutline).intercept { chain ->
                try {
                    val tileOutlineField = sepQSResPoolClazz
                        .getDeclaredField("_mediaPanelOutline")
                        .apply { isAccessible = true }
                    val context = ContextHelper.getContext(classLoader = classLoader)
                    val outline = getCustomOutline.invoke(
                        null, context,
                        cornerRadiusDp.dpToPx(context = context)
                    )
                    // 直接 setValue 替换
                    val mutableStateFlow = tileOutlineField.get(null)
                    val setValueMethod = mutableStateFlow::class.java
                        .getMethod("setValue", Any::class.java)
                    setValueMethod.invoke(mutableStateFlow, outline)
                    log(
                        module = module, tag = QS_TILE_MEDIA_LOG,
                        message = "✅ 成功修改控制中心媒体磁贴圆角半径为 $cornerRadiusDp dp"
                    )
                    return@intercept chain.proceed()
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_MEDIA_LOG,
                        message = "❌ 修改控制中心媒体磁贴圆角半径失败",
                        throwable = e
                    )
                    return@intercept chain.proceed()
                }
            }


            val seedlingMediaDataClazz = loadClass(
                className = "com.oplus.systemui.seedlingservice.mediaControl.SeedlingMediaData",
                classLoader = classLoader
            )
            val getArtworkBgColorMethod = seedlingMediaDataClazz
                .getDeclaredMethod("getArtworkBgColor")
            module.hook(getArtworkBgColorMethod).intercept { chain ->
                return@intercept "#00FFFFFF"
            }

            /*val aaaClazz = loadClass(
                className = "com.oplusos.systemui.common.util.NotifiAndQsPlatformBlurExKt",
                classLoader = classLoader
            )
            val bbb = aaaClazz.getDeclaredMethod(
                "panelBlurRadius",
                Context::class.java
            )
            module.hook(bbb).intercept { chain ->
                return@intercept 160
            }*/

            /*val viewBlurManagerClazz = loadClass(
                className = "com.oplus.systemui.notification.blur.ViewBlurManager",
                classLoader = classLoader
            )
            val requireBlurProxyForViewMethod = viewBlurManagerClazz
                .getDeclaredMethod(
                    "requireBlurProxyForView",
                    View::class.java,
                    loadClass(
                        className = "com.oplus.systemui.notification.blur" +
                                ".ViewBlurManager\$CardType",
                        classLoader = classLoader
                    ),
                    Float::class.javaPrimitiveType
                )
            module.hook(requireBlurProxyForViewMethod).intercept { chain ->
                val blurProxy = chain.proceed()
                val blurConfig = blurProxy
                    .getAnyField<Any>(fieldName = "blurConfig")
                blurConfig.setAnyField(
                    fieldName = "blurRadius",
                    value = 800
                )
                log(
                    module = module, tag = QS_TILE_MEDIA_LOG,
                    message = blurConfig.javaClass.name,
                )
                return@intercept blurProxy
            }*/

            /*val notificationBkgView = loadClass(
                className = "com.android.systemui.statusbar.notification.row." +
                        "NotificationBackgroundView",
                classLoader = classLoader
            )
            val setCustomBkg = notificationBkgView.getDeclaredMethod(
                "setCustomBackground",
                Drawable::class.java
            )*/
            /*module.hook(setCustomBkg).intercept { chain ->
                /*val params = chain.args.toMutableList()
                val context = ContextHelper.getContext(classLoader = classLoader)
                val outline = getCustomOutline.invoke(
                    null, context,
                    cornerRadiusDp.dpToPx(context = context)
                )
                params[0] = outline
                val paramsResult = params.toTypedArray()
                return@intercept chain.proceed(paramsResult)*/
            }*/

            val notificationRowBinderExtImpClazz = loadClass(
                className = "com.oplus.systemui.statusbar.notification.collection." +
                        "NotificationRowBinderExtImp",
                classLoader = classLoader
            )
            val bindRowForBlurControllerMethod = notificationRowBinderExtImpClazz
                .getDeclaredMethod(
                    "bindRowForBlurController",
                    loadClass(
                        className = "com.android.systemui.statusbar.notification.row." +
                                "ExpandableNotificationRow",
                        classLoader = classLoader
                    ),
                    loadClass(
                        className = "com.android.systemui.statusbar.notification.collection." +
                                "NotificationEntry",
                        classLoader = classLoader
                    )
                )
            val cardTypeClazz = loadClass(
                className = "com.oplus.systemui.notification.blur.ViewBlurManager\$CardType",
                classLoader = classLoader
            )
            /*module.hook(bindRowForBlurControllerMethod).intercept { chain ->
                // 执行 super(), 生成 BlurProxy
                chain.proceed()
                // 当前类实例
                val thisObject = chain.thisObject
                val expandableNotificationRow = chain.args[0] as View
                val backgroundNormal =
                    expandableNotificationRow.getAnyField<View>(fieldName = "mBackgroundNormal")
                val context = ContextHelper.getContext(classLoader = classLoader)
                val viewBlurManager = thisObject.getAnyField<Any>(fieldName = "viewBlurManager")
                val notificationType = cardTypeClazz.getStaticField<Any>(fieldName = "NOTIFICATION")
                // 反射调用 requireBlurProxyForView 获取 Proxy
                val viewBlurProxy = viewBlurManager.callAnyMethod<Any>(
                    methodName = "requireBlurProxyForView",
                    paramTypes = arrayOf(
                        View::class.java,
                        cardTypeClazz,
                        Float::class.javaPrimitiveType
                    ),
                    backgroundNormal,
                    notificationType,
                    1.1f
                )
                val blurConfig = viewBlurProxy.callAnyMethod<Any>(methodName = "getBlurConfig")
                blurConfig.callAnyMethod<Any>(
                    methodName = "setCornerRadius",
                    paramTypes = arrayOf(Float::class.javaPrimitiveType),
                    99f
                )
                val calculateGradientStrokeToolsInstance = calculateGradientStrokeToolsClazz
                    .getStaticField<Any>(fieldName = "INSTANCE")
                val strokeParams = calculateGradientStrokeToolsInstance.callAnyMethod<Any>(
                    methodName = "getGradientStrokeParams",
                    paramTypes = arrayOf(
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType,
                        Float::class.javaPrimitiveType,
                        Boolean::class.javaPrimitiveType
                    ),
                    backgroundNormal.width,
                    backgroundNormal.height,
                    36f,
                    false
                )
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
                viewBlurProxy.callAnyMethod<Any>(methodName = "applyBlurConfig")
                backgroundNormal.postInvalidate()
            }*/


// 🔥 1. 强力接管 updateRadius 方法，拦截系统每一次因为滑动、状态变更引发的圆角刷新

            val notificationCornerRadius = 99f

        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_MEDIA_LOG,
                message = "❌ 修改控制中心媒体磁贴圆角半径失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心媒体磁贴圆角半径
     * 适配 ColorOS V16.0.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    private fun modifyCornerRadiusOS160(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        val classLoader = param.classLoader
        try {
            val getBgOutlineProviderByView = loadClass(
                className = "com.oplus.systemui.qs.media" +
                        ".OplusQsBaseMediaPanelView\$getBgOutlineProviderByView$1",
                classLoader = classLoader
            )
            val apply = getBgOutlineProviderByView.getDeclaredMethod(
                "apply",
                Object::class.java
            )
            module.hook(apply).intercept { chain ->
                try {
                    val context = ContextHelper.getContext(classLoader = classLoader)
                    val radius = cornerRadiusDp.dpToPx(context = context)
                    log(
                        module = module, tag = QS_TILE_MEDIA_LOG,
                        message = "✅ 成功修改控制中心媒体磁贴圆角半径为 $cornerRadiusDp dp"
                    )
                    return@intercept radius
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_MEDIA_LOG,
                        message = "❌ 修改控制中心媒体磁贴圆角半径失败",
                        throwable = e
                    )
                    return@intercept chain.proceed()
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_MEDIA_LOG,
                message = "❌ 修改控制中心媒体磁贴圆角半径失败",
                throwable = e
            )
        }
    }
}