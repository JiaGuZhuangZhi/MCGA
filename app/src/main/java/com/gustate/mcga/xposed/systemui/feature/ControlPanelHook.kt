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
        try {
            // 动态获取 ID
            val resIdFixed = ResourceHelper
                .getIdentifier(
                    classLoader,
                    "com.android.systemui",
                    "dimen",
                    "qs_panel_status_bar_margin_top_fixed"
                )
            val resIdMax = ResourceHelper
                .getIdentifier(
                    classLoader,
                    "com.android.systemui",
                    "dimen",
                    "qs_panel_status_bar_margin_top_max"
                )
            val resIdMin = ResourceHelper
                .getIdentifier(
                    classLoader,
                    "com.android.systemui",
                    "dimen",
                    "qs_panel_status_bar_margin_top_min"
                )

            if (resIdFixed != 0) ResourceHelper
                .hookDimensionPixelSize(
                    module, classLoader,
                    resIdFixed, marginTopDp
                )
            if (resIdMax != 0) ResourceHelper
                .hookDimensionPixelSize(
                    module, classLoader,
                    resIdMax, marginTopDp
                )
            if (resIdMin != 0) ResourceHelper
                .hookDimensionPixelSize(
                    module, classLoader,
                    resIdMin, marginTopDp
                )

            log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "✅ 控制中心状态栏边距已设置为 ${marginTopDp}dp"
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
            val context = ContextHelper.getContext(param.classLoader)
            val cellHeightPx = cellHeightDp.dpToPx(context).roundToInt()

            // 1. 修改布局行高插件逻辑
            val cellSizeClass = loadClass(
                "com.oplus.systemui.plugins.qs.CellCalculatorManager\$CellSize",
                param.classLoader
            )
            if (cellSizeClass != null) {
                val getCellHeightMethod = cellSizeClass.getDeclaredMethod("getCellHeight")
                // 使用 intercept 替代 XC_MethodReplacement
                module.hook(getCellHeightMethod).intercept {
                    log(
                        module = module, tag = CONTROL_PANEL_LOG,
                        message = "✅ 磁贴高度计算已拦截: ${cellHeightPx}px"
                    )
                    cellHeightPx
                }
            }

            // 计算复合高度 (复用原逻辑)
            val customHeightPx =
                ((cellHeightDp * 2) - (cellHeightDp - 61)).dpToPx(context).roundToInt()

            // 2. 修改媒体卡片行高 (OplusQsBaseMediaPanelView)
            val mediaPanelViewClass = loadClass(
                "com.oplus.systemui.qs.media.OplusQsBaseMediaPanelView",
                param.classLoader
            )
            if (mediaPanelViewClass != null) {
                val onMeasure = mediaPanelViewClass.getDeclaredMethod(
                    "onMeasure",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                module.hook(onMeasure).intercept { chain ->
                    val result = chain.proceed()
                    (chain.thisObject as View).updateLayoutParams {
                        height = customHeightPx
                    }
                    result
                }
            }

            // 3. 修改 SeekBar 容器高度 (OplusQsBaseToggleSliderLayout)
            val sliderLayoutClass = loadClass(
                "com.oplus.systemui.qs.base.seek.OplusQsBaseToggleSliderLayout",
                param.classLoader
            )
            if (sliderLayoutClass != null) {
                val onAttachedToWindow = sliderLayoutClass.getDeclaredMethod("onAttachedToWindow")
                module.hook(onAttachedToWindow).intercept { chain ->
                    val result = chain.proceed()
                    (chain.thisObject as View).updateLayoutParams {
                        height = customHeightPx
                    }
                    result
                }
            }

        } catch (e: Exception) {
            log(
                module = module, tag = CONTROL_PANEL_LOG,
                message = "❌ Hook 控制中心高度失败: ${e.message}"
            )
        }
    }
}

/*import android.view.View
import androidx.core.view.updateLayoutParams
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.helper.ResourceHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.math.roundToInt

class ControlPanelHook {

    /**
     * 修改控制中心状态栏上外边距
     * qs_panel_status_bar_margin_top_fixed 0x7f0717c4
     * qs_panel_status_bar_margin_top_max   0x7f0717c5
     * qs_panel_status_bar_margin_top_min   0x7f0717c6
     */
    fun hookQsPanelStatusBarMarginTop(
        marginTopDp: Float
    ) {
        val logTag = "CellSize"
        try {
            ResourceHelper.hookDimensionPixelSize(0x7f0717c4, marginTopDp)
            ResourceHelper.hookDimensionPixelSize(0x7f0717c5, marginTopDp)
            ResourceHelper.hookDimensionPixelSize(0x7f0717c6, marginTopDp)
        } catch (e: Exception) {
            log(
                tag = logTag,
                message = "❌ 修改修改控制中心磁贴紧凑程度失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看"
            )
        }
    }

    /**
     * 修改控制中心网格高度 (不剪切内容)
     */
    fun hookCellHeight(
        lpparam: XC_LoadPackage.LoadPackageParam,
        cellHeightDp: Float
    ) {
        val logTag = "ControlPanel"
        try {
            // 修改布局行高
            val cellSizeClass = XposedHelpers.findClass(
                "com.oplus.systemui.plugins.qs.CellCalculatorManager\$CellSize",
                lpparam.classLoader
            )
            // Hook getCellHeight
            XposedBridge.hookMethod(
                cellSizeClass.getDeclaredMethod("getCellHeight"),
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        val context = ContextHelper.getContext()
                        val cellHeightPx = cellHeightDp.dpToPx(context).roundToInt()
                        log(
                            message = "✅ 控制中心磁贴高度已修改为 " +
                                    "${cellHeightDp}dp ($cellHeightPx px)",
                            tag = logTag
                        )
                        return cellHeightPx
                    }
                }
            )
            // 修改媒体卡片行高
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.qs.media.OplusQsBaseMediaPanelView",
                lpparam.classLoader,
                "onMeasure",
                Int::class.java,
                Int::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        super.afterHookedMethod(param)
                        val view = param.thisObject
                        val customHeightPx =
                            ((cellHeightDp * 2) - (cellHeightDp - 61))
                                .dpToPx(ContextHelper.getContext())
                        (view as View).updateLayoutParams {
                            height = customHeightPx.roundToInt()
                        }
                    }
                }
            )
            // 修改那俩 Seekbar 行高
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.qs.base.seek.OplusQsBaseToggleSliderLayout",
                lpparam.classLoader,
                "onAttachedToWindow",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val layout = param.thisObject as View
                        val customHeightPx =
                            ((cellHeightDp * 2) - (cellHeightDp - 61))
                                .dpToPx(ContextHelper.getContext())
                        layout.updateLayoutParams {
                            height = customHeightPx.roundToInt()
                        }
                    }
                }
            )
        } catch (e: Exception) {
            log(
                message = "❌ Hook getCellHeight 失败: ${e.message}",
                tag = logTag,
                throwable = e
            )
        }
    }

}*/