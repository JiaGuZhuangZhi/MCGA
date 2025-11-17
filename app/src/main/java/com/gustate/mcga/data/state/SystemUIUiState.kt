package com.gustate.mcga.data.state

import androidx.compose.ui.graphics.Color

data class SystemUIUiState(
    val enableCustomQsDetail: Boolean = false,
    val qsDetailBkgCoverColor: Int = 0X80000000.toInt(),
    val qsDetailBkgBlurRadius: Int = 255,
    val qsDetailBkgCornerRadius: Float = 28.0f
)