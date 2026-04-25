package com.gustate.mcga.xposed.systemui.feature.qs.tile

import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_MEDIA_LOG
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 控制中心拖动条磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class SliderTileHook {

    /**
     * 修改控制中心拖动条磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun modifyCornerRadius(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        val classLoader = param.classLoader
        try {
            val oplusQsBaseToggleSliderLayoutClass = loadClass(
                className = "com.oplus.systemui.qs.base.seek.OplusQsBaseToggleSliderLayout",
                classLoader = classLoader
            )
            val getRadius = oplusQsBaseToggleSliderLayoutClass
                .getDeclaredMethod("getRadius")
            module.hook(getRadius).intercept { chain ->
                try {
                    val context = ContextHelper.getContext(classLoader = classLoader)
                    val radius = cornerRadiusDp.dpToPx(context = context)
                    log(
                        module = module, tag = QS_TILE_MEDIA_LOG,
                        message = "✅ 成功修改控制中心拖动条磁贴圆角半径为 $cornerRadiusDp dp"
                    )
                    return@intercept radius
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_MEDIA_LOG,
                        message = "❌ 修改控制中心拖动条磁贴圆角半径失败",
                        throwable = e
                    )
                    return@intercept chain.proceed()
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_MEDIA_LOG,
                message = "❌ 修改控制中心拖动条磁贴圆角半径失败",
                throwable = e
            )
        }
    }

}