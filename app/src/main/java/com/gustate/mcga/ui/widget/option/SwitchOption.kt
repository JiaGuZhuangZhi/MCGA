package com.gustate.mcga.ui.widget.option

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.ThemeType
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.gustateThemes
import com.gustate.mcga.ui.theme.isMaterialTheme
import com.gustate.mcga.ui.theme.optionDescriptionTextStyle
import com.gustate.mcga.ui.theme.optionTitleTextStyle

@Composable
fun SwitchOption(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isImportant: Boolean = false,
    isSelect: Boolean = false,
    isNavigation: Boolean = false,
    onRightIconBounds: (LayoutCoordinates) -> Unit = {},
) {

    Row(
        modifier = modifier
            .height(height = 56.dp)
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) }
            )
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
        Box(
            modifier = Modifier
                .weight(weight = 1f)
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

        Switch(
            modifier = Modifier
                .height(18.dp),
            enabled = enabled,
            checked = checked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = gustateColors.switchThumbChecked,
                checkedTrackColor = gustateColors.switchTrackChecked,
                checkedBorderColor =
                    if (isMaterialTheme) SwitchDefaults.colors().checkedBorderColor
                    else Color.Transparent,
                uncheckedThumbColor = gustateColors.switchThumbUnchecked,
                uncheckedTrackColor = gustateColors.switchTrackUnchecked,
                uncheckedBorderColor =
                    if (isMaterialTheme) SwitchDefaults.colors().uncheckedBorderColor
                    else Color.Transparent,
            ),
            thumbContent =
                if (gustateThemes.themeType == ThemeType.Material) {
                    if (checked) {
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.check),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        {
                            Icon(
                                painter = painterResource(id = R.drawable.close),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                } else {
                    {
                        Spacer(modifier = Modifier.size(SwitchDefaults.IconSize))
                    }
                },
            onCheckedChange = onCheckedChange
        )

    }
}