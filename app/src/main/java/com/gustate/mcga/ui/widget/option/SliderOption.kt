package com.gustate.mcga.ui.widget.option

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.dialog.TextFieldDialog
import com.gustate.mcga.ui.theme.ThemeType
import com.gustate.mcga.ui.theme.gustateThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderOption(
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    value: Float,
    valueLabel: String = "%.2f".format(value),
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        TextFieldDialog(
            painter = painter,
            painterDescription = description,
            title = title,
            description = description,
            initialValue = value.toString(),
            onDismissRequest = {
                showDialog = false
            },
            onConfirmation = {
                onValueChange(it)
                showDialog = false
            }
        )
    }
    Column(
        modifier = Modifier
            .clickable(
                enabled = enabled,
                onClick = {
                    showDialog = true
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (icon != null)
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    imageVector = icon,
                    contentDescription = null,
                )
            else if (painter != null)
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
                    painter = painter,
                    contentDescription = null,
                )
            else
                Spacer(modifier = Modifier.size(24.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    description?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Text(
                    text = valueLabel,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    top = 0.dp,
                    start = 56.dp,
                    end = 18.dp,
                    bottom = 16.dp
                )
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            Slider(
                value = value,
                valueRange = valueRange,
                steps = steps,
                onValueChange = onValueChange,
                onValueChangeFinished = {
                    haptic.performHapticFeedback(HapticFeedbackType.ToggleOn)
                },
                modifier = Modifier
                    .weight(1f),
                enabled = enabled,
                interactionSource = interactionSource,
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        thumbTrackGapSize = (-6).dp
                    )
                },
                thumb = { sliderState ->

                    val interactions = remember { mutableStateListOf<Interaction>() }
                    LaunchedEffect(key1 = interactionSource) {
                        interactionSource.interactions.collect { interaction ->
                            when (interaction) {
                                is PressInteraction.Press -> interactions.add(interaction)
                                is PressInteraction.Release -> interactions.remove(element = interaction.press)
                                is PressInteraction.Cancel -> interactions.remove(element = interaction.press)
                                is DragInteraction.Start -> interactions.add(element = interaction)
                                is DragInteraction.Stop -> interactions.remove(element = interaction.start)
                                is DragInteraction.Cancel -> interactions.remove(element = interaction.start)
                            }
                        }
                    }

                    val gustateHeight by animateDpAsState(
                        targetValue =
                            if (interactions.isNotEmpty()) 16.dp
                            else 12.dp,
                    )
                    val gustateWidth by animateDpAsState(
                        targetValue =
                            if (interactions.isNotEmpty()) 16.dp
                            else 12.dp,
                    )

                    val materialHeight = 44.dp
                    val materialWidth by animateDpAsState(
                        targetValue =
                            if (interactions.isNotEmpty()) 2.dp
                            else 4.dp
                    )


                    val thumbSize = DpSize(
                        height = when (gustateThemes.themeType) {
                            is ThemeType.Gustate -> gustateHeight
                            is ThemeType.Ios -> gustateHeight
                            is ThemeType.Material -> materialHeight
                        },
                        width = when (gustateThemes.themeType) {
                            is ThemeType.Gustate -> gustateWidth
                            is ThemeType.Ios -> gustateWidth
                            is ThemeType.Material -> materialWidth
                        }
                    )
                    Spacer(
                        modifier = Modifier
                            .size(thumbSize)
                            .hoverable(interactionSource = interactionSource)
                            .background(Color.White, shape = CircleShape)
                    )
                },
            )
        }
    }
}
