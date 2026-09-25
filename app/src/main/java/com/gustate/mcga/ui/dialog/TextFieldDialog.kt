package com.gustate.mcga.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gustate.mcga.R

@Composable
fun TextFieldDialog(
    painter: Painter? = null,
    painterDescription: String? = null,
    title: String,
    description: String? = null,
    initialValue: String = "",
    onDismissRequest: () -> Unit,
    onConfirmation: (Float) -> Unit
) {
    val context = LocalContext.current
    var inputText by remember(initialValue) { mutableStateOf(initialValue) }
    BaseDialog(
        painter = painter,
        painterDescription = painterDescription,
        title = title,
        description = description,
        onDismissRequest = onDismissRequest,
        onConfirmation = {
            val value = inputText.toFloatOrNull()?.takeIf { it.isFinite() }
            if (value != null) {
                onConfirmation(value)
            } else {
                Toast.makeText(
                    context,
                    R.string.invalid_number,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    ) {
        TextField(
            modifier = Modifier
                .padding(horizontal = 12.dp),
            value = inputText,
            onValueChange = {
                inputText = it
            },
            keyboardOptions = KeyboardOptions
                .Default
                .copy(keyboardType = KeyboardType.Decimal)
        )
    }
}
