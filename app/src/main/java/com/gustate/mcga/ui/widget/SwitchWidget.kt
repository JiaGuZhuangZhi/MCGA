package com.gustate.mcga.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R

@Composable
fun SwitchWidget(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    painter: Painter? = null,
    title: String,
    description: String? = null,
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isError: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = {
                    if (enabled) {
                        onCheckedChange(!checked)
                    }
                }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null)
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        else if (painter != null)
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically),
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        else
            Spacer(modifier = Modifier.size(24.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                description?.let {
                    Text(
                        text = it,
                        color = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Switch(
            enabled = enabled,
            checked = checked,
            thumbContent = if (checked) {
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
            },
            onCheckedChange = null
        )
    }
}