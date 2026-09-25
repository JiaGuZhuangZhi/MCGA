package com.gustate.mcga.ui.widget.option

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.optionDescriptionTextStyle
import com.gustate.mcga.ui.theme.optionTitleTextStyle
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun MenuOption(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    isImportant: Boolean = false,
    menus: List<Menu> = emptyList(),
    selectedMenuIndex: Int = 0,
    onMenuIndexChanged: (Int) -> Unit = {}
) {

    var expanded by remember { mutableStateOf(value = false) }
    var bounds by remember { mutableStateOf<IntRect?>(value = null) }

    val transitionState = remember {
        MutableTransitionState(initialState = false)
    }

    transitionState.targetState = expanded

    // 更明显的弹簧 overshoot 感
    val bouncyEnter = scaleIn(
        initialScale = 0.68f,
        transformOrigin = TransformOrigin(1f, 0f),
        animationSpec = spring(
            dampingRatio = 0.56f,
            stiffness = Spring.StiffnessLow
        )
    )

    val bouncyExit = scaleOut(
        targetScale = 0.64f,
        transformOrigin = TransformOrigin(1f, 0f),
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = Spring.StiffnessMediumLow
        )
    ) + fadeOut(tween(140))

    RichOption(
        modifier = modifier,
        icon = icon,
        painter = painter,
        title = title,
        description = description,
        rightText = menus[selectedMenuIndex].title,
        enabled = enabled,
        isImportant = isImportant,
        isNavigation = false,
        isSelect = true,
        onRightIconBounds = {
            bounds = it.boundsInWindow().roundToIntRect()
        },
        onClick = {
            expanded = !expanded
        }
    )

    if (!transitionState.isIdle || transitionState.currentState) {
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    return IntOffset(
                        x = (bounds?.right ?: anchorBounds.right) - popupContentSize.width, // 右对齐
                        y = bounds?.bottom ?: anchorBounds.bottom                           // 下对齐
                    )
                }
            },
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true)
        ) {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = bouncyEnter,
                exit = bouncyExit
            ) {
                Column(
                    modifier = Modifier
                        .dropShadow(
                            shape = ContinuousRoundedRectangle(size = 18.dp),
                            shadow = Shadow(
                                radius = 6.dp,
                                color = if (isSystemInDarkTheme()) Color(0x20000000) else Color(
                                    0x10000000
                                ),
                                offset = DpOffset(x = 1.dp, y = 1.dp)
                            )
                        )
                        .clip(shape = ContinuousRoundedRectangle(size = 18.dp))
                        .background(
                            color = gustateColors.container,
                            shape = ContinuousRoundedRectangle(size = 18.dp)
                        )
                ) {
                    menus.forEachIndexed { index, menu ->
                        MenuItem(
                            modifier = Modifier,
                            selected = selectedMenuIndex == index,
                            title = menu.title,
                            description = menu.description,
                            enabled = menu.enabled,
                            onClick = {
                                onMenuIndexChanged(index)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color =
                    if (selected) gustateColors.filledTonalButton
                    else Color.Transparent
            )
            .widthIn(max = 180.dp)
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement
            .spacedBy(space = 16.dp)
    ) {

        // 标题与简介
        Box(
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
        ) {
            Column {
                Text(
                    text = title,
                    style = optionTitleTextStyle()
                )
                description?.let {
                    Text(
                        text = it,
                        style = optionDescriptionTextStyle()
                    )
                }
            }
        }

        // 图标设置
        Image(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(id = R.drawable.check),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color =
                    if (selected) gustateColors.onFilledTonalButton
                    else Color.Transparent
            )
        )
    }
}

data class Menu(
    val title: String,
    val description: String? = null,
    val enabled: Boolean = true
)