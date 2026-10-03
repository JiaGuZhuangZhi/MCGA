package com.gustate.mcga.ui.widget.toast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustate.mcga.ui.theme.gustateBlurStyles
import com.gustate.mcga.ui.theme.gustateColors
import com.kyant.capsule.ContinuousRoundedRectangle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect

/**
 * Toast 基础容器组件
 * 提供统一的 Toast 布局结构与视觉样式, 包括毛玻璃效果、阴影、圆角以及图标内容布局
 * 根据屏幕尺寸自适应横向与纵向排布方式，用于承载不同类型的提示内容。
 * @param modifier [Modifier]
 * @param iconPainter Toast 图标绘制资源
 * @param iconPadding 图标内部内容间距，用于控制图标视觉留白
 * @param iconDescription 图标无障碍描述信息
 * @param isLargeScreen 是否为大屏设备，用于切换横向/纵向布局结构
 * @param hazeState 毛玻璃效果状态控制对象
 * @param content Toast 内容区域的可组合内容
 * @see HazeState
 * @see gustateBlurStyles
 * @see gustateColors
 */
@Composable
fun BaseToast(
    modifier: Modifier = Modifier,
    iconPainter: Painter,
    iconPadding: Dp,
    iconDescription: String,
    isLargeScreen: Boolean,
    hazeState: HazeState,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val layout = LocalLayoutDirection.current
    val bottomBarHeight = with(receiver = density) {
        WindowInsets.statusBars.getBottom(density).toDp()
    }
    val rightBarHeight = with(receiver = density) {
        WindowInsets.statusBars.getRight(density, layoutDirection = layout).toDp()
    }
    val toastBlurStyles = gustateBlurStyles.toastBlurStyle()

    val toastModifier = modifier
        .padding(
            bottom = bottomBarHeight + 92.dp,
            end = if (isLargeScreen) rightBarHeight + 36.dp else 0.dp
        )
        .dropShadow(
            shape = ContinuousRoundedRectangle(size = 16.dp),
            shadow = Shadow(
                radius = 8.dp,
                spread = 3.dp,
                color = gustateColors.onContainerPrimary.copy(alpha = 0.08f)
            )
        )
        .clip(shape = ContinuousRoundedRectangle(size = 16.dp))
        .hazeEffect(state = hazeState) {
            blurEffect {
                style = toastBlurStyles
            }
        }
        .widthIn(max = 280.dp)
        .padding(
            vertical = 12.dp,
            horizontal = 16.dp
        )

    if (!isLargeScreen) {
        Row(
            modifier = toastModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = iconPainter,
                contentDescription = iconDescription,
                modifier = Modifier
                    .size(size = 24.dp)
                    .border(
                        width = 0.3.dp,
                        color = gustateColors.container,
                        shape = ContinuousRoundedRectangle(size = 8.dp)
                    )
                    .background(
                        color = gustateColors.container.copy(alpha = 0.8f),
                        shape = ContinuousRoundedRectangle(size = 8.dp)
                    )
                    .padding(all = iconPadding)
            )
            Spacer(
                modifier = Modifier
                    .width(width = 12.dp)
            )
            content()
        }
    } else {
        Column(
            modifier = toastModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = iconPainter,
                    contentDescription = iconDescription,
                    modifier = Modifier
                        .size(size = 28.dp)
                        .border(
                            width = 0.3.dp,
                            color = gustateColors.container,
                            shape = ContinuousRoundedRectangle(size = 8.dp)
                        )
                        .background(
                            color = gustateColors.container.copy(alpha = 0.8f),
                            shape = ContinuousRoundedRectangle(size = 8.dp)
                        )
                        .padding(all = iconPadding)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "温馨提示",
                    color = gustateColors.onContainerPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(
                modifier = Modifier
                    .height(6.dp)
            )
            content()
        }
    }
}