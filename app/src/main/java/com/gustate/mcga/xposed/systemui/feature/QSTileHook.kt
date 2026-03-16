package com.gustate.mcga.xposed.systemui.feature

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.systemui.feature.qs.tile.TwoXOneTileHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class QSTileHook() {

    companion object {
        // LOG TAG 定义
        const val QS_ONE_X_ONE_TILE_LOG = "QsTile 1x1"
        const val QS_RESIZEABLE_TILE_LOG = "QsTile Resizeable"

        const val QS_TILE_2X1_LOG = "控制中心 2*1 磁贴"
    }

    private val twoXOneTileHook = TwoXOneTileHook()

    fun hookQsOneXOneTile(
        lpparam: XC_LoadPackage.LoadPackageParam,
        bkgCornerRadius: Float
    ) {
        try {
            // OplusQSResizeableTileViewOneXOne
            val tileViewClass = XposedHelpers.findClass(
                "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSResizeableTileViewOneXOne",
                lpparam.classLoader
            )
            // Hook getViewRadius() 返回圆角（px）
            XposedBridge.hookMethod(
                tileViewClass.getDeclaredMethod("getViewRadius"),
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        val view = param.thisObject
                        val context = XposedHelpers
                            .getObjectField(view, "mContext") as? Context
                            ?: return param
                        val radiusPx = bkgCornerRadius.dpToPx(context)
                        return radiusPx
                    }
                }
            )
            // Hook createBgOutlineProvider 禁用 circleShape
            XposedBridge.hookMethod(
                tileViewClass.getDeclaredMethod(
                    "createBgOutlineProvider",
                    View::class.java
                ),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val outlineProvider = param.result
                        // 调用 setCircleShape(false)
                        val setCircleShapeMethod = outlineProvider.javaClass.getMethod(
                            "setCircleShape",
                            Boolean::class.java
                        )
                        setCircleShapeMethod.invoke(outlineProvider, false)
                    }
                }
            )
            log(
                message = "✅ QsTile1x1 已修改为半径为 " +
                        "$bkgCornerRadius dp 的圆角矩形",
                tag = QS_ONE_X_ONE_TILE_LOG
            )
        } catch (e: Exception) {
            log(
                message = "❌ 修改 QsTile 1x1 失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看",
                tag = QS_ONE_X_ONE_TILE_LOG
            )
        }
    }

    fun hookQsTileOneXOneRowColumns(
        lpparam: XC_LoadPackage.LoadPackageParam,
        columns: Int
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.plugins.qs.CellCalculatorManager",
                lpparam.classLoader,
                "setNoPersonalRowCountPort",
                Int::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        super.beforeHookedMethod(param)
                        param?.args[0] = columns
                    }
                }
            )
            log(
                message = "✅ QsTile1x1 列表已修改为 $columns 行",
                tag = QS_ONE_X_ONE_TILE_LOG
            )
        } catch (e: Exception) {
            log(
                message = "❌ 修改 QS Tile 1x1 行数失败" +
                        "错误信息: ${e.message}," +
                        "详情可在 com.gustate.mcga 中查看",
                tag = QS_ONE_X_ONE_TILE_LOG
            )
        }
    }

    /**
     * Hook 控制中心 2*1 磁贴
     * @param lpparam 应用程式基础信息
     * @param cornerRadiusDp 2*1 磁贴圆角半径
     * @param fillTileStateFullBkg 使磁贴状态填满控制中心 2*1 磁贴
     * @param hideTileIconBkg 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * @param tileIconSizeDp 控制中心 2*1 磁贴图标大小
     * @param inactiveTitleColor 控制中心 2*1 磁贴非激活/不可用状态下标题颜色
     * @param inactiveDesColor 控制中心 2*1 磁贴非激活/不可用状态标签颜色
     * @param activeTitleColor 控制中心 2*1 磁贴激活状态下标题颜色
     * @param activeDesColor 控制中心 2*1 磁贴激活状态标签颜色
     */
    fun hookTwoXOneTile(
        lpparam: XC_LoadPackage.LoadPackageParam,
        cornerRadiusDp: Float?,
        fillTileStateFullBkg: Boolean?,
        hideTileIconBkg: Boolean?,
        tileIconSizeDp: Float?,
        @ColorInt inactiveTitleColor: Int,
        @ColorInt inactiveDesColor: Int,
        @ColorInt activeTitleColor: Int,
        @ColorInt activeDesColor: Int
    ) {
        // 修改控制中心 2*1 磁贴圆角半径
        cornerRadiusDp?.let {
            twoXOneTileHook.modifyCornerRadius(
                lpparam = lpparam,
                cornerRadiusDp = it
            )
        }
        // 使磁贴状态填满控制中心 2*1 磁贴
        if (fillTileStateFullBkg == true) {
            twoXOneTileHook.modifyTileStateFullBkg(
                lpparam = lpparam
            )
        }
        // 隐藏控制中心 2*1 磁贴图标背景 (状态)
        if (hideTileIconBkg == true) {
            twoXOneTileHook.hideTileIconBkg(
                lpparam = lpparam
            )
        }
        // 修改控制中心 2*1 磁贴图标大小
        tileIconSizeDp?.let {
            twoXOneTileHook.modifyTileIconSize(
                lpparam = lpparam,
                iconSizeDp = it
            )
        }
        // 修改控制中心 2*1 磁贴字体颜色
        twoXOneTileHook.modifyTileTextColor(
            lpparam = lpparam,
            inactiveTitleColor = inactiveTitleColor,
            inactiveDesColor = inactiveDesColor,
            activeTitleColor = activeTitleColor,
            activeDesColor = activeDesColor
        )
    }


}