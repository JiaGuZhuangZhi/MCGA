package com.gustate.mcga.ui.widget.appbar

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.gustateBlurStyles
import com.gustate.mcga.ui.theme.gustateColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import kotlin.math.roundToInt

/**
 * 可滚动折叠的 TopAppBar，支持背景图、模糊效果、标题位置与字号随滚动平滑变化
 *
 * AppBar 在展开与折叠状态之间，根据 toolbarOffsetPx 自动插值：
 * - 高度在 [expandHeight] 与 [collapseHeight] 之间变化
 * - 标题在左下展开态与居中折叠态之间移动
 * - 标题字号在 [expandTitleSize] 与 [collapseTitleSize] 之间缩放
 *
 * @param modifier 外部修饰符，用于设置布局、点击、动画等
 * @param hazeState 模糊效果状态，由外部统一管理 (如页面级 hazeState)
 * @param topBarState 顶栏状态
 * @param title AppBar 标题文本
 * @param backgroundPainter 可选背景图 Painter，仅在展开区域可见
 * @param expandHeight 展开状态下 AppBar 的总高度 (包含工具栏)
 * @param expandTitleSize 展开状态下标题字号
 * @param expandTitleMarginStart 展开状态下标题距左边缘的起始边距
 * @param expandTitleMarginBottom 展开状态下标题距底部的边距
 * @param collapseHeight 折叠状态下工具栏高度
 * @param collapseTitleSize 折叠状态下标题字号
 * @param iconMargin 导航图标与 AppBar 边缘的水平间距
 * @param iconPadding 导航图标内部留白，用于控制可点击区域大小
 * @param rightIcon 右侧操作区内容插槽 (如更多按钮、菜单)
 * @param onLeftIconClick 左侧按钮点击回调
 * @param onRightIconClick 左侧按钮点击回调
 * @param leftIcon 左侧导航图标插槽，默认显示返回箭头
 */
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    topBarState: TopBarState,
    title: String = stringResource(id = R.string.app_name),
    backgroundPainter: Painter? = null,
    expandHeight: Dp = 116.dp,
    expandTitleSize: TextUnit = 30.sp,
    expandTitleMarginStart: Dp = 28.dp,
    expandTitleMarginBottom: Dp = 18.dp,
    collapseHeight: Dp = 60.dp,
    collapseTitleSize: TextUnit = 20.sp,
    iconMargin: Dp = 14.dp,
    iconPadding: Dp = 6.dp,
    rightIcon: @Composable () -> Unit = { },
    onLeftIconClick: () -> Unit = { },
    onRightIconClick: () -> Unit = { },
    leftIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = stringResource(id = R.string.back),
            tint = gustateColors.onBackgroundPrimary
        )
    }
) {

    val density = LocalDensity.current
    var toolBarWidthPx by remember { mutableIntStateOf(value = 0) }
    var titleWidthPx by remember { mutableIntStateOf(value = 0) }
    var toolBarHeightPx by remember { mutableIntStateOf(value = 0) }
    var titleHeightPx by remember { mutableIntStateOf(value = 0) }

    val maxOffsetPx = with(receiver = density) {
        expandHeight.roundToPx() - collapseHeight.roundToPx()
    }.toFloat()

    val rawFraction =
        (-topBarState.toolbarOffsetPx.floatValue / maxOffsetPx)
            .coerceIn(0f, 1f)
    val fraction by animateFloatAsState(
        targetValue = rawFraction,
        animationSpec = spring(
            dampingRatio = 0.64f,
            stiffness = 800f
        )
    )

    val appBarHeight =
        lerp(
            start = expandHeight,
            stop = collapseHeight,
            fraction = fraction
        )
    topBarState.appBarHeight = appBarHeight

    val topBarBlurStyle = gustateBlurStyles.topBarBlurStyle()
    val topBarBlurProgressive = gustateBlurStyles.topBarBlurProgressive()

    val expandedX = with(receiver = density) {
        expandTitleMarginStart.roundToPx()
    }.toFloat()

    val collapsedX =
        ((toolBarWidthPx - titleWidthPx) / 2f)
            .coerceAtLeast(minimumValue = 0f)

    val titleOffsetX =
        lerp(
            start = expandedX,
            stop = collapsedX,
            fraction = fraction
        )

    val expandedY = with(receiver = density) {
        (-expandTitleMarginBottom).roundToPx().toFloat()
    }

    val collapsedY = -((toolBarHeightPx - titleHeightPx) / 2f)

    val titleOffsetY =
        lerp(
            start = expandedY,
            stop = collapsedY,
            fraction = fraction
        )

    val titleSize =
        lerp(
            start = expandTitleSize,
            stop = collapseTitleSize,
            fraction = fraction
        )

    Box(
        modifier = modifier
            .hazeEffect(state = hazeState) {
                blurEffect {
                    style = topBarBlurStyle
                    progressive = topBarBlurProgressive
                }
            }
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .height(appBarHeight)
                .fillMaxWidth()
                .animateContentSize()
        ) {
            if (backgroundPainter != null)
                Image(
                    painter = backgroundPainter,
                    contentDescription = "background",
                    contentScale = ContentScale.FillBounds
                )
            // 自定义应用栏
            Row(
                modifier = Modifier
                    .height(collapseHeight)
                    .fillMaxWidth()
                    .onSizeChanged {
                        toolBarWidthPx = it.width
                        toolBarHeightPx = it.height
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 导航图标
                IconButton(
                    modifier = Modifier
                        .padding(start = iconMargin)
                        .size(size = collapseHeight - iconPadding),
                    onClick = { onLeftIconClick() }
                ) {
                    leftIcon()
                }
                Text(
                    text = title,
                    color = Color.Transparent,
                    modifier = Modifier
                        .onSizeChanged {
                            titleWidthPx = it.width
                            titleHeightPx = it.height
                        },
                    fontSize = collapseTitleSize
                )
                IconButton(
                    modifier = Modifier
                        .padding(end = iconMargin)
                        .size(size = collapseHeight - iconPadding),
                    onClick = { onRightIconClick() }
                ) {
                    rightIcon()
                }
            }

            // title 定义
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    color = gustateColors.onBackgroundPrimary,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = titleOffsetX.roundToInt(),
                                y = titleOffsetY.roundToInt()
                            )
                        },
                    fontSize = titleSize
                )
            }
        }
    }
}