package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.HomeKeys
import com.gustate.mcga.data.state.HomeUiState

class HomeViewModel(context: Application) : AndroidViewModel(context) {
    private val repo = XposedRepo(context)

    private val _uiState = mutableStateOf(
        value = HomeUiState(
            enableDockBlur = repo.getBoolean(HomeKeys.ENABLE_DOCK_BLUR),
            hideDrawerName = repo.getBoolean(HomeKeys.HIDE_DRAWER_NAME)
        )
    )
    val uiState: MutableState<HomeUiState> = _uiState

    fun updateDockBlur(enabled: Boolean) {
        repo.setBoolean(HomeKeys.ENABLE_DOCK_BLUR, enabled)
        _uiState.value = _uiState.value.copy(enableDockBlur = enabled)
    }

    fun updateHideAppName(enabled: Boolean) {
        repo.setBoolean(HomeKeys.HIDE_DRAWER_NAME, enabled)
        _uiState.value = _uiState.value.copy(hideDrawerName = enabled)
    }
}