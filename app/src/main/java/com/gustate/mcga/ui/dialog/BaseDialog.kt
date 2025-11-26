package com.gustate.mcga.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun BaseDialog(
    painter: Painter,
    painterDescription: String? = null,
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = ContinuousRoundedRectangle(24.dp),
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painter,
                    contentDescription = painterDescription,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .size(size = 28.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    content()
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 8.dp,
                            start = 8.dp,
                            end = 4.dp
                        ),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 8.dp,
                            start = 4.dp,
                            end = 16.dp
                        ),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}