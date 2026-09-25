package com.gustate.mcga.panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gustate.mcga.data.setting.base.SettingNode

class SettingState<T>(
    val node: SettingNode,
    initialValue: T
) {
    var value: T by mutableStateOf(initialValue)
}