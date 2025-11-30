package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.AodKeys
import com.gustate.mcga.data.state.AodUiState

class AodViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = XposedRepo(context)

    private val _uiState = mutableStateOf(
        value = AodUiState(
            enableAodPanoramicAllDay = repo.getBoolean(
                key = AodKeys.ENABLE_AOD_PANORAMIC_ALL_DAY,
                def = false
            ),
            enableAllDayAodSettings = repo.getBoolean(
                key = AodKeys.ENABLE_ALL_DAY_AOD_SETTINGS,
                def = false
            )
        )
    )
    val uiState: MutableState<AodUiState> get() = _uiState

    fun updateEnableAodPanoramicAllDay(value: Boolean) {
        repo.setBoolean(
            key = AodKeys.ENABLE_AOD_PANORAMIC_ALL_DAY,
            value = value
        )
        _uiState.value = _uiState.value.copy(enableAodPanoramicAllDay = value)
    }

    fun updateEnableAllDayAodSettings(value: Boolean) {
        repo.setBoolean(
            key = AodKeys.ENABLE_ALL_DAY_AOD_SETTINGS,
            value = value
        )
        _uiState.value = _uiState.value.copy(enableAllDayAodSettings = value)
    }

}