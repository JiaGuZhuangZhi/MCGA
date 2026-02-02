package com.gustate.mcga.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.dialog.TextFieldDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWidget(
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    value: Float,
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
                    text = "%.2f"
                        .format(value),
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
                enabled = enabled
            )
        }
    }
}