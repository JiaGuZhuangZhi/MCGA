package com.gustate.mcga.data.state

data class SystemUIUiState(
    val enableCustomQsPanelLayout: Boolean,
    val qsPanelStatusBarMarginTop: Float,
    val qsPanelCellHeight: Float,
    val enableCustomQsTileOneXOne: Boolean,
    val qsTileOneXOneCornerRadius: Float,
    val qsTileOneXOneRowColumns: Int,
    val enableCustomQsDetail: Boolean,
    val qsDetailBkgCoverColor: Int,
    val qsDetailFrgCoverColor: Int,
    val qsDetailBkgBlurRadius: Int,
    val qsDetailBkgCornerRadius: Float,
    // 控制中心 2*1 磁贴
    val enableCustomQsResizeableTile: Boolean,
    val qsResizeableTileCornerRadius: Float,
    val qsTwoXOneTileFillStateFullBkg: Boolean,
    val qsTwoXOneTileHideIconBkg: Boolean,
    val qsTwoXOneTileIconSize: Float,
    val qsTwoXOneTileInactiveTitleColor: Int,
    val qsTwoXOneTileActiveTitleColor: Int,
    val qsTwoXOneTileInactiveDesColor: Int,
    val qsTwoXOneTileActiveDesColor: Int,
    // 控制中心媒体磁贴
    val enableCustomQsMediaTile: Boolean,
    val qsMediaTileCornerRadius: Float,
    // 控制中心拖动条磁贴
    val enableCustomQsSliderTile: Boolean,
    val qsSliderTileCornerRadius: Float,
    // AOD
    val enableAodPanoramicAllDay: Boolean,
    val enableAllDayAodSettings: Boolean
)