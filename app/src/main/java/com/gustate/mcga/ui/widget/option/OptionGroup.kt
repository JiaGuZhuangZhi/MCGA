package com.gustate.mcga.ui.widget.option

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.ThemeType
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.gustateThemes
import com.gustate.mcga.ui.theme.optionGroupTitleTextStyle
import com.kyant.capsule.ContinuousRoundedRectangle

/**
 * OptionGroup 选项组容器
 * @param modifier 作用于容器根 Column
 * @param title 选项组名称
 * @param content 容器中内容组件列表, 并返回容器形状
 */
@Composable
fun OptionGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: List<@Composable (Shape) -> Unit>,
) {

    if (content.isEmpty()) return

    // 圆角配置
    val bigCorner by animateDpAsState(
        targetValue = when (gustateThemes.themeType) {
            ThemeType.Gustate -> 18.dp
            ThemeType.Ios -> 18.dp
            ThemeType.Material -> 18.dp
        }
    )
    val smallCorner by animateDpAsState(
        targetValue = when (gustateThemes.themeType) {
            ThemeType.Gustate -> 0.dp
            ThemeType.Ios -> 0.dp
            ThemeType.Material -> 8.dp
        }
    )

    // 形状配置
    val middleListShape = remember(key1 = smallCorner) {
        ContinuousRoundedRectangle(size = smallCorner)
    }
    val singleShape = remember(key1 = bigCorner) {
        ContinuousRoundedRectangle(size = bigCorner)
    }
    val firstListShape = remember(key1 = smallCorner, key2 = bigCorner) {
        ContinuousRoundedRectangle(
            topStart = bigCorner,
            topEnd = bigCorner,
            bottomStart = smallCorner,
            bottomEnd = smallCorner
        )
    }
    val endListShape = remember(key1 = smallCorner, key2 = bigCorner) {
        ContinuousRoundedRectangle(
            topStart = smallCorner,
            topEnd = smallCorner,
            bottomStart = bigCorner,
            bottomEnd = bigCorner
        )
    }

    // 间隙配置
    val space by animateDpAsState(
        targetValue = when (gustateThemes.themeType) {
            ThemeType.Gustate -> 0.dp
            ThemeType.Ios -> 0.dp
            ThemeType.Material -> 2.dp
        }
    )

    Column(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 16.dp
            )
    ) {

        // 选项组标题
        title?.let {
            Text(
                text = title,
                style = optionGroupTitleTextStyle(),
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 8.dp
                    )
            )
        }

        // 选项卡容器
        Column(
            modifier = Modifier
                .clip(singleShape),
            verticalArrangement = Arrangement
                .spacedBy(space)
        ) {
            content.forEachIndexed { index, itemContent ->

                // 为不同位置的选项卡设置不同的形状
                val shape = when {
                    content.size == 1 -> singleShape
                    index == 0 -> firstListShape
                    index == content.size - 1 -> endListShape
                    else -> middleListShape
                }

                Column(
                    modifier = Modifier
                        .background(color = gustateColors.container, shape = shape)
                        .clip(shape = shape)
                ) {
                    itemContent(shape)
                }

            }
        }

    }
}