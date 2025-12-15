package com.gustate.mcga.xposed.systemui.feature

import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.helper.ResourceHelper
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.math.roundToInt

object ControlPanel {

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
            throw e
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
            // 加载内部类
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
        } catch (e: Exception) {
            log(
                message = "❌ Hook getCellHeight 失败: ${e.message}",
                tag = logTag,
                throwable = e
            )
            throw e
        }
    }

}