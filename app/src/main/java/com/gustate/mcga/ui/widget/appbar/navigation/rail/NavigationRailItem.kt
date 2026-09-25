package com.gustate.mcga.ui.widget.appbar.navigation.rail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustate.mcga.ui.theme.gustateColors
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun NavigationRailItem(
    painter: Painter,
    label: String,
    selected: Boolean = false,
    cornerRadius: Dp = CornerRadius,
    itemColors: NavigationRailItemColors =
        NavigationRailItemColors.defaultNavigationRailItemColors()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = ContinuousRoundedRectangle(size = cornerRadius))
            .background(
                color =
                    if (selected) itemColors.selectedBackgroundColor
                    else itemColors.unselectedBackgroundColor
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tintColor =
            if (selected) itemColors.selectedIconColor
            else itemColors.unselectedIconColor
        Icon(
            modifier = Modifier
                .size(size = 28.dp),
            painter = painter,
            contentDescription = null,
            tint = tintColor
        )
        Spacer(
            modifier = Modifier
                .width(12.dp)
        )
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = tintColor
        )
    }
}

/**
 * 表示导航项组件中各个元素所使用的颜色
 * @param selectedBackgroundColor 选中状态下导航项背景颜色
 * @param selectedIconColor 选中状态下导航项图标颜色
 * @param selectedTextColor 选中状态下导航项标签颜色
 * @param unselectedBackgroundColor 非选中状态下导航项背景颜色
 * @param unselectedIconColor 非选中状态下导航项图标颜色
 * @param unselectedTextColor 非选中状态下导航项标签颜色
 */
data class NavigationRailItemColors(
    val selectedBackgroundColor: Color,
    val selectedIconColor: Color,
    val selectedTextColor: Color,
    val unselectedBackgroundColor: Color,
    val unselectedIconColor: Color,
    val unselectedTextColor: Color,
) {
    companion object {
        /**
         * 导航项组件中各个元素所使用的颜色的默认值
         */
        @Composable
        fun defaultNavigationRailItemColors() = NavigationRailItemColors(
            selectedBackgroundColor = gustateColors.background,
            selectedIconColor = gustateColors.onBackgroundPrimary,
            selectedTextColor = gustateColors.onBackgroundPrimary,
            unselectedBackgroundColor = gustateColors.container,
            unselectedIconColor = gustateColors.onBackgroundSecondary,
            unselectedTextColor = gustateColors.onBackgroundSecondary,
        )
    }
}

private val CornerRadius = 18.dp