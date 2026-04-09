package com.gustate.mcga.xposed.systemui.feature

import android.view.View
import androidx.core.view.updateLayoutParams
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.helper.ResourceHelper
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import kotlin.math.roundToInt

/**
 * 控制中心 (Control Panel) 相关功能拦截
 */
class ControlPanelHook {

    companion object {
        const val CONTROL_PANEL_LOG = "控制中心整体布局"
    }

    /**
     * 修改控制中心状态栏上外边距
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param marginTopDp 目标外边距 (单位: dp)
     */
    fun hookQsPanelStatusBarMarginTop(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        marginTopDp: Float
    ) {
        val classLoader = param.classLoader
        val resNames = listOf(
            "qs_panel_status_bar_margin_top_fixed",
            "qs_panel_status_bar_margin_top_max",
            "qs_panel_status_bar_margin_top_min"
        )
        try {
            ResourceHelper.getIdentifier(
                module = module,
                classLoader = classLoader,
                pkgName = "com.android.systemui",
                resType = "dimen",
                resNames = resNames,
                onReadyResIds = { resIds ->
                    // 分类正确与错误的 ids
                    val (valid, invalid) = resIds.entries.partition { it.value != 0 }
                    // 错误报日志
                    invalid.forEach {
                        log(
                            module = module, tag = CONTROL_PANEL_LOG,
                            message = "❌ 获取 ${it.key} 资源 ID 失败 (ID 为 0)"
                        )
                    }
                    // 走流程
                    valid.forEach {
                        ResourceHelper.hookDimensionPixelSize(
                            module = module,
                            classLoader = classLoader,
                            resId = it.value,
                            newSizeDp = marginTopDp
                        )
                    }
                }
            )
            log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "✅ 控制中心状态栏边距已设置为 $marginTopDp dp"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 修改控制中心边距失败: ${e.message}"
            )
        }
    }

    /**
     * 修改控制中心网格高度 (磁贴紧凑程度)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cellHeightDp 磁贴高度 (单位: dp)
     */
    fun hookCellHeight(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cellHeightDp: Float
    ) {
        try {
            // 修改布局行高插件逻辑
            val cellSizeClass = loadClass(
                className = "com.oplus.systemui.plugins.qs.CellCalculatorManager\$CellSize",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 未获取到 CellCalculatorManager\$CellSize 类"
            )
            val getCellHeightMethod = cellSizeClass.getDeclaredMethod(
                "getCellHeight"
            ) ?: return log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 未获取到 getCellHeight 方法"
            )
            module.hook(getCellHeightMethod).intercept {
                val context = ContextHelper.getContext(classLoader = param.classLoader)
                val cellHeightPx = cellHeightDp.dpToPx(context).roundToInt()
                log(
                    module = module, tag = CONTROL_PANEL_LOG,
                    message = "✅ 磁贴高度计算已拦截: $cellHeightPx px"
                )
                cellHeightPx
            }

            // 修改媒体卡片行高 (OplusQsBaseMediaPanelView)
            val mediaPanelViewClass = loadClass(
                className = "com.oplus.systemui.qs.media.OplusQsBaseMediaPanelView",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 未获取到 OplusQsBaseMediaPanelView 类"
            )
            val onMeasure = mediaPanelViewClass.getDeclaredMethod(
                "onMeasure",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            ) ?: return log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 未获取到 onMeasure 方法"
            )
            module.hook(onMeasure).intercept { chain ->
                val result = chain.proceed()
                val context = ContextHelper.getContext(classLoader = param.classLoader)
                // 计算媒体卡片高度
                val customHeightPx =
                    ((cellHeightDp * 2) - (cellHeightDp - 61)).dpToPx(context).roundToInt()
                (chain.thisObject as View).updateLayoutParams { height = customHeightPx }
                log(
                    module = module, tag = CONTROL_PANEL_LOG,
                    message = "✅ 媒体卡片高度已修改: $customHeightPx px"
                )
                result
            }

            // 修改 SeekBar 容器高度
            val sliderLayoutClass = loadClass(
                className = "com.oplus.systemui.qs.base.seek.OplusQsBaseToggleSliderLayout",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 未获取到 OplusQsBaseToggleSliderLayout 类"
            )
            val onAttachedToWindow = sliderLayoutClass.getDeclaredMethod(
                "onAttachedToWindow"
            ) ?: return log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ 未获取到 onAttachedToWindow 方法"
            )
            module.hook(onAttachedToWindow).intercept { chain ->
                val result = chain.proceed()
                val context = ContextHelper.getContext(classLoader = param.classLoader)
                // 计算 SeekBar 高度
                val customHeightPx =
                    ((cellHeightDp * 2) - (cellHeightDp - 61)).dpToPx(context).roundToInt()
                (chain.thisObject as View).updateLayoutParams { height = customHeightPx }
                log(
                    module = module, tag = CONTROL_PANEL_LOG,
                    message = "✅ 媒体卡片高度已修改: $customHeightPx px"
                )
                result
            }
        } catch (e: Exception) {
            log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ Hook 控制中心高度失败: ${e.message}"
            )
        }
    }
}