package com.gustate.mcga.data.state

data class WalletUiState(
    val enableCustomNfcCardPage: Boolean = false,
    val nfcCardPageBkgBlurRadius: Int = 255,
    val nfcCardPageBkgScrimLight: Int = 0X14FFFFFF,
    val nfcCardPageBkgScrimDark: Int = 0X66000000,
    val nfcCardPageWidgetSquircle: Boolean = true,
    val nfcCardPageWidgetAlpha: Float = 0.42f,
    val nfcCardPageWidgetCornerRadius: Float = 24f
)