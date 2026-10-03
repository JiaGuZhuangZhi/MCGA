package com.gustate.mcga.ui.widget.text

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gustate.mcga.ui.theme.gustateColors
import com.gustate.mcga.ui.theme.inputTextStyle
import com.kyant.capsule.ContinuousRoundedRectangle

/**
 * @param hint: 空字符时的提示
 * @param startIcon: 左侧图标;
 */
@Composable
fun Input(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    hint: String,
    hintColor: Color = gustateColors.onContainerSecondary,
    startIcon: Painter? = null,
    endIcon: Painter? = null,
    onEndIconClick: () -> Unit = {},
    iconMargin: Dp = 16.dp,
    contentMargin: Dp = 12.dp,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = inputTextStyle(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    cursorBrush: Brush = SolidColor(value = gustateColors.onFilledTonalButton)
) {
    // 是否有焦点 (控制右侧按钮)
    var hasFocus by remember { mutableStateOf(value = false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { hasFocus = it.isFocused },
        singleLine = true,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        cursorBrush = cursorBrush,
        decorationBox = @Composable { innerTextField ->
            val showStartIcon = startIcon != null
            val showEndIcon = hasFocus && value.isNotEmpty() && endIcon != null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 60.dp)
                    .background(
                        color = gustateColors.container,
                        shape = ContinuousRoundedRectangle(size = 16.dp)
                    )
                    .border(
                        width = if (hasFocus) 2.dp else 0.dp,
                        color =
                            if (hasFocus) gustateColors.onFilledTonalButton
                            else Color.Transparent,
                        shape = ContinuousRoundedRectangle(size = 16.dp)
                    )
                    .padding(
                        start =
                            if (showStartIcon) iconMargin else 0.dp,
                        end =
                            if (showEndIcon) iconMargin - 8.dp else 0.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧 Icon
                if (showStartIcon)
                    Icon(
                        painter = startIcon,
                        tint = inputTextStyle().color,
                        contentDescription = null
                    )
                Box(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .padding(horizontal = contentMargin)
                ) {
                    // 当空字符时, 显示hint
                    if (value.isEmpty())
                        Text(
                            text = hint,
                            color = hintColor,
                            style = textStyle
                        )
                    // 原本输入框的内容
                    innerTextField()
                }
                // 存在焦点 且 有输入内容时 且 有资源传入 显示右侧按钮
                if (showEndIcon)
                    IconButton(
                        onClick = onEndIconClick,
                        modifier = Modifier
                            .onFocusChanged {
                                hasFocus = true
                            }
                    ) {
                        Icon(
                            painter = endIcon,
                            contentDescription = null,
                            tint = inputTextStyle().color
                        )
                    }
            }
        }
    )
}

fun pwKeyboardOptions() = KeyboardOptions.Default.copy(
    capitalization = KeyboardCapitalization.None,
    keyboardType = KeyboardType.Password
)