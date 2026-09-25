package com.gustate.mcga.ui.widget.snackbar

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Snackbar 管理器
 * 2026/01/09
 */
object SnackbarManager {

    // 消息队列
    private val channel = Channel<SnackbarMessage>(
        // 还没显示的时候把消息先存起来
        capacity = Channel.BUFFERED
    )

    /**
     * 显示 Snackbar (使用模型)
     * @param message SnackbarMessage 模型
     * @see SnackbarMessage 模型
     */
    fun show(message: SnackbarMessage) {
        channel.trySend(message)
    }

    /**
     * 显示 Snackbar
     * @param message 信息文本
     * @param action 操作文本
     * @param duration 持续时间
     * @param onAction 操作
     */
    fun show(
        message: String,
        action: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null
    ) {
        show(
            SnackbarMessage(
                message = message,
                action = action,
                duration = duration,
                onAction = onAction
            )
        )
    }

    /**
     * 每收到一条 Channel 消息射一次 Flow
     * @return 射出来的 Flow 咯
     */
    internal fun messagesFlow(): Flow<SnackbarMessage> {
        return channel.receiveAsFlow()
    }

}
