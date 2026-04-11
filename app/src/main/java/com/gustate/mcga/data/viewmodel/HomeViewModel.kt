package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.HomeKeys
import com.gustate.mcga.data.state.HomeUiState

class HomeViewModel(context: Application) : AndroidViewModel(context) {
    private val repo = XposedRepo.getInstance(context = context)

    private val _uiState = mutableStateOf(
        value = HomeUiState(
            enableDockBkg = repo.getBoolean(HomeKeys.ENABLE_DOCK_BKG, false),
            enableDockBlur = repo.getBoolean(HomeKeys.ENABLE_DOCK_BLUR, false),
            dockBlurRadius = repo.getInt(HomeKeys.DOCK_BLUR_RADIUS, 800),
            dockCornerRadius = repo.getFloat(HomeKeys.DOCK_CORNER_RADIUS, 28f),
            hideDrawerName = repo.getBoolean(HomeKeys.HIDE_DRAWER_NAME, false),
            clearAllButton = repo.getBoolean(HomeKeys.CLEAR_ALL_BUTTON, false)
        )
    )
    val uiState: MutableState<HomeUiState> = _uiState

    fun updateDockBkg(enabled: Boolean) {
        repo.setBoolean(HomeKeys.ENABLE_DOCK_BKG, enabled)
        _uiState.value = _uiState.value.copy(enableDockBkg = enabled)
    }

    fun updateDockBlur(enabled: Boolean) {
        repo.setBoolean(HomeKeys.ENABLE_DOCK_BLUR, enabled)
        _uiState.value = _uiState.value.copy(enableDockBlur = enabled)
    }

    fun updateDockBlurRadius(value: Int) {
        repo.setInt(HomeKeys.DOCK_BLUR_RADIUS, value)
        _uiState.value = _uiState.value.copy(dockBlurRadius = value)
    }

    fun updateDockCornerRadius(value: Float) {
        repo.setFloat(HomeKeys.DOCK_CORNER_RADIUS, value)
        _uiState.value = _uiState.value.copy(dockCornerRadius = value)
    }

    fun updateHideAppName(enabled: Boolean) {
        repo.setBoolean(HomeKeys.HIDE_DRAWER_NAME, enabled)
        _uiState.value = _uiState.value.copy(hideDrawerName = enabled)
    }

    fun updateClearAllButton(enabled: Boolean) {
        repo.setBoolean(HomeKeys.CLEAR_ALL_BUTTON, enabled)
        _uiState.value = _uiState.value.copy(clearAllButton = enabled)
    }
}