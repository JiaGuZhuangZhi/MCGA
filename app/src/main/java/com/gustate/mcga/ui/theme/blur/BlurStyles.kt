package com.gustate.mcga.ui.theme.blur

import androidx.compose.runtime.Composable
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeProgressive

/**
 * BlurStyles 接口类
 * 定义了需要具体实现的 [HazeBlurStyle] 函数
 * @see HazeBlurStyle
 * @see com.gustate.mcga.ui.theme.GustateTheme
 */
interface BlurStyles {

    /**
     * 顶栏模糊样式空函数
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    fun topBarBlurStyle(): HazeBlurStyle

    /**
     * 顶栏模糊渐变蒙版空函数
     * @return [HazeProgressive.LinearGradient] 实例
     * @see HazeProgressive.LinearGradient
     */
    @Composable
    fun topBarBlurProgressive(): HazeProgressive.LinearGradient

    /**
     * 底栏模糊样式空函数
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    fun bottomBarBlurStyle(): HazeBlurStyle

    /**
     * 底栏模糊渐变蒙版空函数
     * @return [HazeProgressive.LinearGradient] 实例
     * @see HazeProgressive.LinearGradient
     */
    @Composable
    fun bottomBarBlurProgressive(): HazeProgressive.LinearGradient

    /**
     * 填充按钮模糊样式空函数
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    fun filledButtonBlurStyle(): HazeBlurStyle

    /**
     * 卡片模糊样式空函数
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    fun cardBlurStyle(): HazeBlurStyle

    /**
     * 对话框模糊样式空函数
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    fun dialogBlurStyle(): HazeBlurStyle

    /**
     * Toast 模糊样式空函数
     */
    @Composable
    fun toastBlurStyle(): HazeBlurStyle

    /**
     * 表单模糊样式空函数
     * @return [HazeBlurStyle] 实例
     * @see HazeBlurStyle
     */
    @Composable
    fun sheetBlurStyle(): HazeBlurStyle

}