package com.gustate.mcga.data.state

data class SystemUIUiState(
    val enableCustomQsDetail: Boolean = false,
    val qsDetailBkgCoverColor: Int = 0X80000000.toInt(),
    val qsDetailFrgCoverColor: Int = 0X80000000.toInt(),
    val qsDetailBkgBlurRadius: Int = 255,
    val qsDetailBkgCornerRadius: Float = 28.0f,
    val enableCustomQsResizeableTile: Boolean = false,
    val qsResizeableTileCornerRadius: Float = 28.0f,
    val enableAodPanoramicAllDay: Boolean = false
)