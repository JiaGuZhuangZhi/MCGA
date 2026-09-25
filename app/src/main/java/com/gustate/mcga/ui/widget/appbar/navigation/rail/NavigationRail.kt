package com.gustate.mcga.ui.widget.appbar.navigation.rail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import com.gustate.mcga.R
import com.gustate.mcga.ui.theme.gustateColors
import com.kyant.capsule.ContinuousRoundedRectangle

@Preview
@Composable
fun NavigationRail() {

    val primaryActionButton: @Composable () -> Unit = {}
    val items: @Composable () -> Unit = {}

    val contentPadding = PaddingValues(
        horizontal = 28.dp,
        vertical = 36.dp
    )

    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val systemTopBarHeight = WindowInsets()
        .getBottom(density = density)

    val iosBackgroundModifier = Modifier
        .background(
            color = gustateColors.container,
            shape = ContinuousRoundedRectangle(size = 36.dp)
        )

    Layout(
        modifier = Modifier
            .then(other = iosBackgroundModifier),
        content = {
            Row(
                modifier = Modifier
                    .layoutId(HeaderLayoutIdTag),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.info_outline),
                    contentDescription = null,
                )
            }
            Box(
                modifier = Modifier
                    .layoutId(layoutId = PrimaryActionLayoutIdTag)
            ) {
                Button(
                    shape = ContinuousRoundedRectangle(size = 99.dp),
                    onClick = {

                    }
                ) {

                }
            }
            Column(
                modifier = Modifier
                    .layoutId(NavigationLayoutIdTag)
            ) {
                items()
            }
        }
    ) { measurables, constraints ->

        // 宽松约束 (类似于 View 中的 wrap_content)
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        // 最大高度与宽度的值 (类似于 View 中的 match_parent 绘制后得到的值)
        val layoutHeight = constraints.maxHeight
        val layoutWidth = constraints.maxWidth

        // 取各边距像素值
        val topPadding = contentPadding
            .calculateTopPadding()
            .roundToPx()
        val bottomPadding = contentPadding
            .calculateBottomPadding()
            .roundToPx()
        val startPadding = contentPadding
            .calculateStartPadding(layoutDirection = layoutDirection)
            .roundToPx()
        val endPadding = contentPadding
            .calculateEndPadding(layoutDirection = layoutDirection)
            .roundToPx()

        val contentWidth = layoutWidth - startPadding - endPadding
        val fullWidthConstraints = looseConstraints.copy(
            minWidth = contentWidth,
            maxWidth = contentWidth
        )

        val headerPlaceable = measurables
            .fastFirst { it.layoutId == HeaderLayoutIdTag }
            .measure(constraints = fullWidthConstraints)

        val navigationPlaceable = measurables
            .fastFirst { it.layoutId == NavigationLayoutIdTag }
            .measure(constraints = fullWidthConstraints)

        layout(
            width = layoutWidth,
            height = layoutHeight
        ) {
            headerPlaceable.place(
                x = startPadding,
                y = topPadding + systemTopBarHeight
            )
        }
    }
}

private const val HeaderLayoutIdTag = "header"
private const val PrimaryActionLayoutIdTag = "primaryAction"
private const val NavigationLayoutIdTag = "navigation"