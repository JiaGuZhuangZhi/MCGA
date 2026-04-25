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
import kotlin.math.roundToInt

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
                    val radius = cornerRadiusDp.dpToPx(context = context).roundToInt()
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