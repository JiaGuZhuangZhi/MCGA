package com.gustate.mcga.xposed.systemui.feature.qs.tile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_2X1_LOG
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import kotlin.math.roundToInt

/**
 * 控制中心 2*1 磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class TwoXOneTileHook {

    /**
     * 修改控制中心 2*1 磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 2*1 磁贴圆角半径 (dp)
     */
    fun modifyCornerRadius(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        val clazz = loadClass(
            "com.oplus.systemui.plugins.qs.customize.view.tile." +
                    "OplusQSResizeableTileView",
            param.classLoader
        ) ?: return log(
            module = module, tag = QS_TILE_2X1_LOG,
            message = "❌ 未获取到 OplusQSResizeableTileView 类"
        )
        try {
            val method = clazz.getDeclaredMethod("getRadius")
            module.hook(method).intercept { chain ->
                val view = chain.thisObject as View
                val cornerRadiusPx = cornerRadiusDp.dpToPx(view.context)
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴圆角半径为 $cornerRadiusDp dp"
                )
                cornerRadiusPx
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改 2*1 磁贴圆角半径失败: ${e.message}"
            )
        }
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴 (禁用分离模式)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun modifyTileStateFullBkg(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {

        val clazz = loadClass(
            className = "com.oplus.systemui.qs.base.util.QsColorUtil",
            classLoader = param.classLoader
        ) ?: return log(
            module = module, tag = QS_TILE_2X1_LOG,
            message = "❌ 未获取到 QsColorUtil 类"
        )

        try {
            val method = clazz.getDeclaredMethod(
                "isNeedUseSeparateDarkThemeColor",
                Context::class.java,
                Boolean::class.javaPrimitiveType
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 isNeedUseSeparateDarkThemeColor 函数"
            )

            module.hook(method).intercept { chain ->
                // 获取调用栈，看看是不是那个代理类在调我们
                val stack = Throwable().stackTrace
                val isFromProxy = stack.any {
                    it.className.contains(other = "QsHighlightTileViewBackgroundProxyImpl")
                }
                if (isFromProxy) {
                    log(
                        module = module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 成功使磁贴状态填满 2*1 磁贴"
                    )
                    false
                } else {
                    chain.proceed()
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 填满 2*1 磁贴失败: ${e.message}"
            )
        }
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun hideTileIconBkg(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        try {
            val clazz = loadClass(
                className = "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 OplusQSIconView 类"
            )
            val method = clazz.getDeclaredMethod(
                "setBackground",
                Drawable::class.java
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 setBackground 函数"
            )
            module.hook(method).intercept { chain ->
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态)"
                )
                null
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 隐藏图标背景失败: ${e.message}"
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴图标大小
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param iconSizeDp 图标大小 (dp)
     */
    fun modifyTileIconSize(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        iconSizeDp: Float
    ) {
        try {
            val clazz = loadClass(
                "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
                param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 OplusQSIconView 类"
            )
            val spanSizeClass = loadClass(
                "com.oplusos.systemui.common.model.SpanSize",
                param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 SpanSize 类"
            )
            val method = clazz.getDeclaredMethod(
                "getIconSize",
                spanSizeClass,
                Boolean::class.javaPrimitiveType
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 getIconSize 函数"
            )
            module.hook(method).intercept { chain ->
                val view = chain.thisObject as View
                val iconSizePx = iconSizeDp.dpToPx(view.context).roundToInt()
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴图标大小为 ${iconSizeDp}dp"
                )
                iconSizePx
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改图标大小失败: ${e.message}"
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴标签字体颜色
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param inactiveTitleColor 非激活标题颜色
     * @param inactiveDesColor 非激活描述颜色
     * @param activeTitleColor 激活标题颜色
     * @param activeDesColor 激活描述颜色
     */
    fun modifyTileTextColor(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        @ColorInt inactiveTitleColor: Int,
        @ColorInt inactiveDesColor: Int,
        @ColorInt activeTitleColor: Int,
        @ColorInt activeDesColor: Int
    ) {
        try {
            // 相关类获取
            val labelColorManagerClazz = loadClass(
                className = "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSHighlightTileViewLabelColorManager",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 OplusQSHighlightTileViewLabelColorManager 类"
            )
            val stateClazz = loadClass(
                className = "com.android.systemui.plugins.qs.QSTile\$State",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 QSTile\$State 类"
            )
            val pairClazz = loadClass(
                className = "kotlin.Pair",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 Pair 类"
            )
            val method = labelColorManagerClazz.getDeclaredMethod(
                "getColorByTileState",
                Context::class.java,
                stateClazz
            )
            module.hook(method).intercept { chain ->
                val stateObj = chain.args[1]
                val state = stateObj.getAnyField<Int>("state")
                // 依据 state 获取颜色
                val (titleColor, desColor) = when (state) {
                    2 -> activeTitleColor to activeDesColor // Active
                    else -> inactiveTitleColor to inactiveDesColor // Inactive / Unavailable
                }
                // 反射构造 kotlin.Pair(ColorStateList, ColorStateList)
                val resultPair = pairClazz
                    .getConstructor(Any::class.java, Any::class.java)
                    .newInstance(
                        ColorStateList.valueOf(titleColor),
                        ColorStateList.valueOf(desColor)
                    )
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴文本颜色 (state: $state)"
                )
                resultPair
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改文本颜色失败: ${e.message}"
            )
        }
    }
}