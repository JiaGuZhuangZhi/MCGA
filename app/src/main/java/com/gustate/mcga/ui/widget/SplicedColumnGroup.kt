package com.gustate.mcga.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun SplicedColumnGroup(
    modifier: Modifier = Modifier,
    title: String = "",
    content: List<@Composable (shape: Shape) -> Unit>,
) {

    if (content.isEmpty()) return

    val bigCorner = 18.dp
    val smallCorner = 8.dp

    val firstListShape = ContinuousRoundedRectangle(
        topStart = bigCorner, topEnd = bigCorner,
        bottomStart = smallCorner, bottomEnd = smallCorner)
    val middleListShape = ContinuousRoundedRectangle(smallCorner)
    val endListShape = ContinuousRoundedRectangle(
        topStart = smallCorner, topEnd = smallCorner,
        bottomStart = bigCorner, bottomEnd = bigCorner)
    val singleShape = ContinuousRoundedRectangle(bigCorner)

    Column(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 16.dp
            )
    ) {
        // Group title
        if (title != "")
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
            )
        // The container for setting items.
        Column(
            modifier = Modifier.clip(singleShape),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            content.forEachIndexed { index, itemContent ->
                // Determine the shape based on the pkg's position.
                val shape = when {
                    content.size == 1 -> singleShape
                    index == 0 -> firstListShape
                    index == content.size - 1 -> endListShape
                    else -> middleListShape
                }

                // Apply background with the correct shape to the pkg.
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer, shape)
                        .clip(shape)
                ) {
                    itemContent(shape)
                }
            }
        }
    }
}