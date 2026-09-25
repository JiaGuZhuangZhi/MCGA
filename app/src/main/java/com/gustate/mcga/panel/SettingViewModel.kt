package com.gustate.mcga.panel

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.ColorSetting
import com.gustate.mcga.data.setting.base.CommandSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.IntSetting
import com.gustate.mcga.data.setting.base.LinkSetting
import com.gustate.mcga.data.setting.base.SettingItem
import com.gustate.mcga.data.setting.base.SettingNode
import com.gustate.mcga.utils.RootUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(
    application: Application
) : AndroidViewModel(
    application = application
) {

    private val _settingsState = mutableStateMapOf<SettingNode, SettingState<*>>()
    val settingsState get() = _settingsState

    private val repo = XposedRepo.getInstance(context = application)

    @Suppress("UNCHECKED_CAST")
    fun <T> getState(setting: SettingItem<T>): SettingState<T> {
        val existingState = _settingsState[setting]
        if (existingState != null) return existingState as SettingState<T>

        val initialValue: Any = when (setting) {
            is BooleanSetting -> repo.getBoolean(setting.key, setting.default)
            is ColorSetting -> repo.getInt(setting.key, setting.default)
            is IntSetting -> repo.getInt(setting.key, setting.default)
                .coerceIn(setting.min, setting.max)
            is FloatSetting -> repo.getFloat(setting.key, setting.default)
                .coerceIn(setting.min, setting.max)
            is CommandSetting, is LinkSetting -> setting.default
        }
        val state = SettingState<Any>(setting, initialValue)
        _settingsState[setting] = state
        return state as SettingState<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> updateSetting(setting: SettingItem<T>, value: T) {
        val state = getState(setting)
        when (setting) {
            is BooleanSetting -> {
                val normalized = value as Boolean
                repo.setBoolean(setting.key, normalized)
                state.value = normalized as T
            }

            is ColorSetting -> {
                val normalized = value as Int
                repo.setInt(setting.key, normalized)
                state.value = normalized as T
            }

            is IntSetting -> {
                val normalized = (value as Int).coerceIn(setting.min, setting.max)
                repo.setInt(setting.key, normalized)
                state.value = normalized as T
            }

            is FloatSetting -> {
                val normalized = (value as Float).coerceIn(setting.min, setting.max)
                repo.setFloat(setting.key, normalized)
                state.value = normalized as T
            }

            is CommandSetting, is LinkSetting -> Unit
        }
    }

    fun executeCommand(command: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val succeeded = withContext(Dispatchers.IO) {
                RootUtils.executeRootCommand(command.replace("su -c ", "")) != null
            }
            onComplete(succeeded)
        }
    }
}
