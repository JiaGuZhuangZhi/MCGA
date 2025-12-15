package com.gustate.mcga.data.state

data class SystemUIUiState(
    val enableCustomQsPanelLayout: Boolean = false,
    val qsPanelStatusBarMarginTop: Float = 18.0f,
    val qsPanelCellHeight: Float = 76.0f,
    val enableCustomQsTileOneXOne: Boolean = false,
    val qsTileOneXOneCornerRadius: Float = 24.0f,
    val enableCustomQsDetail: Boolean = false,
    val qsDetailBkgCoverColor: Int = 0X80000000.toInt(),
    val qsDetailFrgCoverColor: Int = 0X80000000.toInt(),
    val qsDetailBkgBlurRadius: Int = 1024,
    val qsDetailBkgCornerRadius: Float = 24.0f,
    val enableCustomQsResizeableTile: Boolean = false,
    val qsResizeableTileCornerRadius: Float = 24.0f,
    val qsResizeableTileIconBkgCornerRadius: Float = 24.0f,
    val qsResizeableTileIconBkgCoverColor: Int = 0X80FFFFFF.toInt(),
    val enableAodPanoramicAllDay: Boolean = false,
    val enableAllDayAodSettings: Boolean = false
)