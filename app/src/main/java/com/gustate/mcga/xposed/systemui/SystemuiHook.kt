package com.gustate.mcga.xposed.systemui

import com.gustate.mcga.data.keys.SystemUIKeys
import com.gustate.mcga.xposed.systemui.feature.Aod
import com.gustate.mcga.xposed.systemui.feature.ControlPanel
import com.gustate.mcga.xposed.systemui.feature.QSTile
import com.gustate.mcga.xposed.systemui.feature.QsDetail
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

object SystemuiHook {

    fun applySystemUiFeature(lpparam: XC_LoadPackage.LoadPackageParam) {

        // 以下内容仅 Hook 系统界面 (com.android.systemui)
        if (lpparam.packageName != "com.android.systemui") return
        val prefs = XSharedPreferences(
            "com.gustate.mcga",
            "xposed_prefs"
        )
        prefs.makeWorldReadable()

        // QS PANEL LAYOUT
        val enableCustomQsPanelLayout = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_PANEL_LAYOUT,
            false
        )
        val qsPanelStatusBarMarginTop = prefs.getFloat(
            SystemUIKeys.QS_PANEL_STATUS_BAR_MARGIN_TOP,
            18.0f
        )
        val qsPanelCellHeight = prefs.getFloat(
            SystemUIKeys.QS_PANEL_CELL_HEIGHT,
            76.0f
        )
        if (enableCustomQsPanelLayout) {
            ControlPanel.hookQsPanelStatusBarMarginTop(
                marginTopDp = qsPanelStatusBarMarginTop
            )
            ControlPanel.hookCellHeight(
                lpparam = lpparam,
                cellHeightDp = qsPanelCellHeight
            )
        }

        // QS TILE 1X1
        val enableCustomQsTileOneXOne = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_TILE_ONE_X_ONE,
            false
        )
        val qsTileOneXOneCornerRadius = prefs.getFloat(
            SystemUIKeys.QS_TILE_ONE_X_ONE_CORNER_RADIUS,
            24.0f
        )
        val qsTileOneXOneRowColumns = prefs.getInt(
            SystemUIKeys.QS_TILE_ONE_X_ONE_ROW_COLUMNS,
            4
        )
        if (enableCustomQsTileOneXOne) {
            QSTile.hookQsOneXOneTile(
                lpparam = lpparam,
                bkgCornerRadius = qsTileOneXOneCornerRadius
            )
            QSTile.hookQsTileOneXOneRowColumns(
                lpparam = lpparam,
                columns = qsTileOneXOneRowColumns
            )
        }

        // Resizeable Tile
        val enableCustomQsResizeableTile = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_RESIZEABLE_TILE,
            false
        )
        val qsResizeableTileCornerRadius = prefs.getFloat(
            SystemUIKeys.QS_RESIZEABLE_TILE_CORNER_RADIUS,
            24.0f
        )
        if (enableCustomQsResizeableTile) {
            QSTile.hookQSResizeableTile(
                lpparam = lpparam,
                bkgCornerRadius = qsResizeableTileCornerRadius
            )
        }

        // QS DETAIL
        val enableCustomQsDetail = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_DETAIL,
            false
        )
        val qsDetailCoverColor = prefs.getInt(
            SystemUIKeys.QS_DETAIL_BKG_COVER_COLOR,
            0X80000000.toInt()
        )
        val qsDetailFrgColor = prefs.getInt(
            SystemUIKeys.QS_DETAIL_FRG_COVER_COLOR,
            0X80000000.toInt()
        )
        val qsDetailBlurRadius = prefs.getInt(
            SystemUIKeys.QS_DETAIL_BKG_BLUR_RADIUS,
            255
        )
        val qsDetailCornerRadius = prefs.getFloat(
            SystemUIKeys.QS_DETAIL_BKG_CORNER_RADIUS,
            24.0f
        )
        if (enableCustomQsDetail) {
            QsDetail.hookQsDetailContainer(
                lpparam = lpparam,
                blurRadius = qsDetailBlurRadius,
                cornerRadius = qsDetailCornerRadius,
                foregroundColor = qsDetailFrgColor,
                backgroundColor = qsDetailCoverColor
            )
        }

        // AOD
        val enableAodPanoramicAllDay = prefs.getBoolean(
            SystemUIKeys.ENABLE_AOD_PANORAMIC_ALL_DAY,
            false
        )
        if (enableAodPanoramicAllDay) {
            Aod.hookPanoramicAodAllDay(lpparam)
        }

    }
}