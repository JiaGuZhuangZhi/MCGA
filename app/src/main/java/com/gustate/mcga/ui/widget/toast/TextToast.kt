package com.gustate.mcga.ui.widget.toast

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.gustateColors
import dev.chrisbanes.haze.HazeState

/**
 * 文本型 Toast 组件
 * 基于 [BaseToast] 实现的轻量提示组件 仅承载文本消息内容
 * 用于展示短时反馈信息，如操作结果、提示或状态通知，并自动适配大屏与小屏布局
 * @param message 要展示的提示文本内容
 * @param isLargeScreen 是否为大屏设备 用于控制 Toast 布局结构
 * @param hazeState 毛玻璃效果状态控制对象，用于背景模糊渲染
 * @see BaseToast
 * @see HazeState
 */
@Composable
fun TextToast(
    message: String,
    isLargeScreen: Boolean,
    hazeState: HazeState
) {
    BaseToast(
        iconPainter = painterResource(id = R.drawable.ic_launcher_foreground),
        iconPadding = 4.dp,
        iconDescription = stringResource(id = R.string.app_name),
        isLargeScreen = isLargeScreen,
        hazeState = hazeState
    ) {
        Text(
            text = message,
            color = gustateColors.onContainerPrimary
        )
    }
}