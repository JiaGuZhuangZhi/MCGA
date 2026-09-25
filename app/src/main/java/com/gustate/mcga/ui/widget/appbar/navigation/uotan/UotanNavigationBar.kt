package com.gustate.mcga.ui.widget.appbar.navigation.uotan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.gustateBlurStyles
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect

@Composable
fun GustateNavigationBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    paddingValues: PaddingValues,
    content: @Composable RowScope.() -> Unit
) {
    val bottomBarBlurStyle = gustateBlurStyles.bottomBarBlurStyle()
    val bottomBarBlurProgressive = gustateBlurStyles.bottomBarBlurProgressive()
    Box(
        modifier = modifier
            .hazeEffect(state = hazeState) {
                blurEffect {
                    style = bottomBarBlurStyle
                    progressive = bottomBarBlurProgressive
                }
            }
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxWidth()
                .height(height = 65.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}