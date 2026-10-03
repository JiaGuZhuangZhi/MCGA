package com.gustate.mcga.ui.theme.blur

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.gustateColors
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.HazeProgressive

/**
 * Blur 样式基类
 * 继承自 [BlurStyles]
 * @see BlurStyles
 * @see HazeBlurStyle
 */
abstract class BaseBlurStyles : BlurStyles {

    /**
     * 生成 [HazeBlurStyle] 的基础方法
     * 提供默认参数, 方便各个组件调用
     * @param blurRadius 模糊半径
     * @param containerColor 模糊内容色
     * @param lightAlpha 浅色模式透明度
     * @param darkAlpha 深色模式透明度
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     * @see BlurStyles
     */
    @Composable
    @ReadOnlyComposable
    protected fun baseBlurStyle(
        blurRadius: Dp = 24.dp,
        containerColor: Color =
            gustateColors.background,
        lightAlpha: Float = 0.36f,
        darkAlpha: Float = 0.48f,
    ) = HazeBlurStyle(
        blurRadius = blurRadius,
        backgroundColor = containerColor,
        colorEffect = HazeColorEffect.tint(
            color = containerColor.copy(
                alpha =
                    if (isSystemInDarkTheme()) lightAlpha
                    else darkAlpha
            )
        )
    )

    /**
     * 生成 [HazeProgressive.LinearGradient] 的基础方法
     * 提供默认参数, 方便各个组件调用
     * @param isVertical 是否垂直渐变
     * @param easing 渐变缓动函数
     * @param startPosition 渐变起始位置
     * @param startIntensity 渐变起始强度
     * @param endX 渐变结束位置
     * @param endIntensity 渐变结束强度
     * @param preferPerformance 是否优先性能优化
     * @return [HazeProgressive.LinearGradient] 实例
     * @see HazeProgressive.LinearGradient
     * @see BlurStyles
     */
    @Composable
    @ReadOnlyComposable
    protected fun baseBlurProgressive(
        isVertical: Boolean,
        easing: Easing = EaseIn,
        startPosition: Float = 0f,
        startIntensity: Float = 1f,
        endX: Float = Float.POSITIVE_INFINITY,
        endIntensity: Float = 0f,
        preferPerformance: Boolean = false
    ): HazeProgressive.LinearGradient {
        return if (isVertical) {
            HazeProgressive.verticalGradient(
                easing = easing,
                startY = startPosition,
                startIntensity = startIntensity,
                endY = endX,
                endIntensity = endIntensity,
                preferPerformance = preferPerformance
            )
        } else {
            HazeProgressive.horizontalGradient(
                easing = easing,
                startX = startPosition,
                startIntensity = startIntensity,
                endX = endX,
                endIntensity = endIntensity,
                preferPerformance = preferPerformance
            )
        }
    }

}