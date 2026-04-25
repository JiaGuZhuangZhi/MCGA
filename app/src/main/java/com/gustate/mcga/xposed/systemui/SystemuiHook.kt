package com.gustate.mcga.xposed.systemui

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.SystemUIKeys
import com.gustate.mcga.data.keys.SystemUIKeys.ENABLE_CUSTOM_QS_PANEL_LAYOUT
import com.gustate.mcga.data.keys.SystemUIKeys.QS_PANEL_CELL_HEIGHT
import com.gustate.mcga.data.keys.SystemUIKeys.QS_PANEL_STATUS_BAR_MARGIN_TOP
import com.gustate.mcga.xposed.systemui.feature.ControlPanelHook
import com.gustate.mcga.xposed.systemui.feature.PanoramicHook
import com.gustate.mcga.xposed.systemui.feature.QSTileHook
import com.gustate.mcga.xposed.systemui.feature.QsDetailHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object SystemuiHook {

    // 实例化相关 Feature 类
    private val controlPanelHook = ControlPanelHook()
    private val panoramicHook = PanoramicHook()
    private val qsDetailHook = QsDetailHook()
    private val qsTileHook = QSTileHook()

    /**
     * 应用系统界面 Hook 设置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applySystemuiFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 以下内容仅 Hook 系统界面 (com.android.systemui)
        if (param.packageName != "com.android.systemui") return

        // 应用配置
        applyControlPanelFeature(module = module, param = param, prefs = prefs)
        applyQsTileOneXOneFeature(module = module, param = param, prefs = prefs)
        applyQsTileTwoXOneFeature(module = module, param = param, prefs = prefs)
        applyQsTileSliderFeature(module = module, param = param, prefs = prefs)
        applyQsTileMediaFeature(module = module, param = param, prefs = prefs)
        applyQsDetailFeature(module = module, param = param, prefs = prefs)
        applyAodFeature(module = module, param = param, prefs = prefs)

    }

    /**
     * 应用控制中心背景配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyControlPanelFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val customQsPanelLayout = prefs.getBoolean(ENABLE_CUSTOM_QS_PANEL_LAYOUT, false)
        val qsPanelTopBarMarginTop = prefs.getFloat(QS_PANEL_STATUS_BAR_MARGIN_TOP, 18.0f)
        val qsPanelCellHeight = prefs.getFloat(QS_PANEL_CELL_HEIGHT, 76.0f)
        if (customQsPanelLayout) {
            controlPanelHook.hookQsPanelStatusBarMarginTop(
                module = module,
                param = param,
                marginTopDp = qsPanelTopBarMarginTop
            )
            controlPanelHook.hookCellHeight(
                module = module,
                param = param,
                cellHeightDp = qsPanelCellHeight
            )
        }
    }

    /**
     * 应用 1*1 磁贴配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyQsTileOneXOneFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
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
            qsTileHook.hookQsOneXOneTile(
                module = module,
                param = param,
                bkgCornerRadius = qsTileOneXOneCornerRadius
            )
            qsTileHook.hookQsTileOneXOneRowColumns(
                module = module,
                param = param,
                columns = qsTileOneXOneRowColumns
            )
        }
    }

    /**
     * 应用 2*1 磁贴配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyQsTileTwoXOneFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // Resizeable Tile
        val enableCustomQsResizeableTile = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_RESIZEABLE_TILE,
            false
        )
        val qsResizeableTileCornerRadius = prefs.getFloat(
            SystemUIKeys.QS_RESIZEABLE_TILE_CORNER_RADIUS,
            24.0f
        )
        val qsTwoXOneTileFillStateFullBkg = prefs.getBoolean(
            SystemUIKeys.QS_TWO_X_ONE_TILE_FILL_STATE_FULL_BKG,
            true
        )
        val qsTwoXOneTileHideIconBkg = prefs.getBoolean(
            SystemUIKeys.QS_TWO_X_ONE_TILE_HIDE_ICON_BKG,
            true
        )
        val qsTwoXOneTileIconSize = prefs.getFloat(
            SystemUIKeys.QS_TWO_X_ONE_TILE_ICON_SIZE,
            28f
        )
        val qsTwoXOneTileInactiveTitleColor = prefs.getInt(
            SystemUIKeys.QS_TWO_X_ONE_TILE_INACTIVE_TITLE_COLOR,
            0XE6ffffff.toInt()
        )
        val qsTwoXOneTileActiveTitleColor = prefs.getInt(
            SystemUIKeys.QS_TWO_X_ONE_TILE_ACTIVE_TITLE_COLOR,
            0XE6000000.toInt()
        )
        val qsTwoXOneTileInactiveDesColor = prefs.getInt(
            SystemUIKeys.QS_TWO_X_ONE_TILE_INACTIVE_DES_COLOR,
            0X89ffffff.toInt()
        )
        val qsTwoXOneTileActiveDesColor = prefs.getInt(
            SystemUIKeys.QS_TWO_X_ONE_TILE_ACTIVE_DES_COLOR,
            0X89000000.toInt()
        )
        if (enableCustomQsResizeableTile) {
            qsTileHook.hookTwoXOneTile(
                module = module,
                param = param,
                cornerRadiusDp = qsResizeableTileCornerRadius,
                fillTileStateFullBkg = qsTwoXOneTileFillStateFullBkg,
                hideTileIconBkg = qsTwoXOneTileHideIconBkg,
                tileIconSizeDp = qsTwoXOneTileIconSize,
                inactiveTitleColor = qsTwoXOneTileInactiveTitleColor,
                inactiveDesColor = qsTwoXOneTileInactiveDesColor,
                activeTitleColor = qsTwoXOneTileActiveTitleColor,
                activeDesColor = qsTwoXOneTileActiveDesColor
            )
        }
    }

    /**
     * 应用拖动条磁贴配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyQsTileSliderFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableCustomQsSliderTile = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_SLIDER_TILE,
            false
        )
        val qsSliderTileCornerRadius = prefs.getFloat(
            SystemUIKeys.QS_SLIDER_TILE_CORNER_RADIUS,
            24f
        )
        if (enableCustomQsSliderTile) {
            qsTileHook.hookSliderTile(
                module = module,
                param = param,
                cornerRadiusDp = qsSliderTileCornerRadius
            )
        }
    }

    /**
     * 应用媒体磁贴配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyQsTileMediaFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 控制中心拖动条磁贴
        val enableCustomQsMediaTile = prefs.getBoolean(
            SystemUIKeys.ENABLE_CUSTOM_QS_MEDIA_TILE,
            false
        )
        val qsMediaTileCornerRadius = prefs.getFloat(
            SystemUIKeys.QS_MEDIA_TILE_CORNER_RADIUS,
            24f
        )
        if (enableCustomQsMediaTile) {
            qsTileHook.hookMediaTile(
                module = module,
                param = param,
                cornerRadiusDp = qsMediaTileCornerRadius
            )
        }
    }

    /**
     * 应用快速设置面板配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyQsDetailFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
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
            qsDetailHook.hookQsDetailContainer(
                module = module,
                param = param,
                blurRadius = qsDetailBlurRadius,
                cornerRadius = qsDetailCornerRadius,
                foregroundColor = qsDetailFrgColor,
                backgroundColor = qsDetailCoverColor
            )
        }
    }

    /**
     * 应用息屏配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyAodFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableAodPanoramicAllDay = prefs.getBoolean(
            SystemUIKeys.ENABLE_AOD_PANORAMIC_ALL_DAY,
            false
        )
        if (enableAodPanoramicAllDay) {
            panoramicHook.hookPanoramicAodAllDay(
                module = module,
                param = param
            )
        }
    }

}