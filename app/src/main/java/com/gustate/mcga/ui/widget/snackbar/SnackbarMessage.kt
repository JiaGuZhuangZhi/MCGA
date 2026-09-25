package com.gustate.mcga.ui.widget.snackbar

import androidx.compose.material3.SnackbarDuration

/**
 * Snackbar 信息模型
 * @param message 信息文本
 * @param action 操作文本
 * @param duration 持续时间
 * @param onAction 操作
 */
data class SnackbarMessage(
    val message: String,
    val action: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val onAction: (() -> Unit)? = null
)