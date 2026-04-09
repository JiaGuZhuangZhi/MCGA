package com.gustate.mcga.data.state

data class HomeUiState(
    val enableDockBkg: Boolean = false,
    val enableDockBlur: Boolean = false,
    val dockBlurRadius: Int = 800,
    val dockCornerRadius: Float = 28f,
    val hideDrawerName: Boolean = false,
    val clearAllButton: Boolean = false
)