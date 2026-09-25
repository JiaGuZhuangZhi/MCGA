package com.gustate.mcga.ui.theme.blur

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.gustateColors
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeProgressive

/**
 * gustate 主题 (默认) BlurStyle
 * 继承自 [BaseBlurStyles]
 * @see BaseBlurStyles
 * @see BlurStyles
 * @see HazeBlurStyle
 * @see com.gustate.mcga.ui.theme.GustateTheme
 */
class GustateBlurStyles : BaseBlurStyles() {

    /**
     * 顶栏模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun topBarBlurStyle() = baseBlurStyle(
        blurRadius = 24.dp,
        containerColor = gustateColors.background,
        lightAlpha = 0.84f,
        darkAlpha = 0.62f
    )

    /**
     * 顶栏渐变模糊蒙版的 gustate 主题实现
     * @return [HazeProgressive.LinearGradient] 实例
     * @see HazeProgressive.LinearGradient
     */
    @Composable
    @ReadOnlyComposable
    override fun topBarBlurProgressive() = baseBlurProgressive(
        isVertical = true,
        startIntensity = 1f,
        endIntensity = 0f
    )

    /**
     * 底栏模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun bottomBarBlurStyle() = baseBlurStyle(
        blurRadius = 24.dp,
        containerColor = gustateColors.container,
        lightAlpha = 0.84f,
        darkAlpha = 0.62f
    )

    /**
     * 顶栏渐变模糊蒙版的 gustate 主题实现
     * @return [HazeProgressive.LinearGradient] 实例
     * @see HazeProgressive.LinearGradient
     */
    @Composable
    @ReadOnlyComposable
    override fun bottomBarBlurProgressive() = baseBlurProgressive(
        isVertical = true,
        startIntensity = 0f,
        endIntensity = 1f
    )

    /**
     * 填充按钮模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun filledButtonBlurStyle() = baseBlurStyle(
        containerColor = gustateColors.filledButton,
        lightAlpha = 0.52f,
        darkAlpha = 0.48f
    )

    /**
     * 卡片模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun cardBlurStyle() = baseBlurStyle(
        blurRadius = 18.dp,
        containerColor = gustateColors.container,
        lightAlpha = 0.92f,
        darkAlpha = 0.86f
    )

    /**
     * 对话框模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun dialogBlurStyle() = baseBlurStyle(
        blurRadius = 18.dp,
        containerColor = gustateColors.container,
        lightAlpha = 0.92f,
        darkAlpha = 0.86f
    )

    /**
     * Toast 模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun toastBlurStyle() = baseBlurStyle(
        blurRadius = 18.dp,
        containerColor = gustateColors.container,
        lightAlpha = 0.92f,
        darkAlpha = 0.86f
    )

    /**
     * 表单模糊样式的 gustate 主题实现
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    @ReadOnlyComposable
    override fun sheetBlurStyle() = baseBlurStyle(
        blurRadius = 48.dp,
        containerColor = gustateColors.background,
        lightAlpha = 0.42f,
        darkAlpha = 0.54f
    )

}