package com.gustate.mcga.ui.widget.option

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.gustateThemes
import com.gustate.mcga.ui.theme.optionDescriptionTextStyle
import com.gustate.mcga.ui.theme.optionTitleTextStyle

/**
 * 丰富选项卡 (图标、标题、介绍、箭头) RichOption
 * @param modifier 作用于根布局 Row, 默认为空 [Modifier]
 * @param icon [ImageVector] 类型的图标 (可 null)
 * @param painter [Painter] 类型的图标 (可 null)
 * ** 上面的两个图标会优先选择 [ImageVector] **
 * @param title 选项卡标题
 * @param description 选项卡简介 (可 null)
 * @param enabled 是否启用, 默认启用
 * @param onClick 点击回调
 * @param isImportant 是否重要 (选项卡简介是否呈现重要色)
 */
@Composable
fun RichOption(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    description: String? = null,
    rightText: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    isImportant: Boolean = false,
    isSelect: Boolean = false,
    isNavigation: Boolean = false,
    onRightIconBounds: (LayoutCoordinates) -> Unit = {},
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement
            .spacedBy(space = 16.dp)
    ) {

        // 图标设置
        when {
            icon != null ->
                Icon(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .align(alignment = Alignment.CenterVertically),
                    imageVector = icon,
                    contentDescription = null,
                    tint =
                        if (gustateThemes.isDynamicColor) gustateColors.filledButton
                        else gustateColors.onContainerPrimary
                )

            painter != null ->
                Icon(
                    modifier = Modifier
                        .size(size = 24.dp)
                        .align(alignment = Alignment.CenterVertically),
                    painter = painter,
                    contentDescription = null,
                    tint =
                        if (gustateThemes.isDynamicColor) gustateColors.filledButton
                        else gustateColors.onContainerPrimary
                )

            else -> {}
        }

        // 标题与简介
        Row(
            modifier = Modifier
                .weight(weight = 1f)
                .align(alignment = Alignment.CenterVertically),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 1f)
            ) {
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
            rightText?.let {
                Text(
                    text = rightText,
                    style = optionDescriptionTextStyle()
                )
            }
        }

        // 右箭头
        if (isNavigation || isSelect) {
            Icon(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .onGloballyPositioned {
                        onRightIconBounds(it)
                    },
                painter = painterResource(
                    id =
                        if (isSelect) R.drawable.settings_filled
                        else R.drawable.settings_outline
                ),
                contentDescription = null,
                tint = gustateColors.onContainerSecondary
            )
        }

    }
}