package com.gustate.mcga.data.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.gustate.mcga.R
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.ModuleKeys
import com.gustate.mcga.data.model.RootManager
import com.gustate.mcga.data.state.ModuleUiState
import com.gustate.mcga.utils.CommonUtils
import com.gustate.mcga.utils.RootUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ModuleViewModel(context: Application) : AndroidViewModel(application = context) {

    private val _repo = XposedRepo.getInstance(context = context)

    private val _uiState = MutableStateFlow(
        value = ModuleUiState(
            isReady = false,
            isModuleActive = false,
            isRootAvailable = false,
            isLogEnabled = true,
            isLauncherIconShowing = true,
            rootManagerInfo = RootManager(
                rootManagerName = R.string.kernelsu,
                rootManagerVer = "100"
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { uiState ->
                val state = context.packageManager.getComponentEnabledSetting(
                    ComponentName(
                        context.packageName,
                        "${CommonUtils.PACKAGE_NAME}.Home"
                    )
                )
                uiState.copy(
                    isRootAvailable = RootUtils.isRootAvailable(),
                    rootManagerInfo = RootUtils.getRootManager(),
                    isLogEnabled = _repo.getBoolean(ModuleKeys.ENABLE_LOG, true),
                    isLauncherIconShowing =
                        state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
                )
            }
        }
        viewModelScope.launch {
            delay(timeMillis = 500)
            if (!_uiState.value.isReady) {
                _uiState.update { state ->
                    state.copy(isReady = true)
                }
            }
        }
        _repo.onActiveChanged = { active ->
            viewModelScope.launch(context = Dispatchers.Main) {
                _uiState.update { state ->
                    state.copy(
                        isModuleActive = active,
                        isReady = true
                    )
                }
            }
        }
    }

    fun updateIsLogEnabled(enabled: Boolean) {
        _repo.setBoolean(ModuleKeys.ENABLE_LOG, enabled)
        _uiState.value = _uiState.value.copy(isLogEnabled = enabled)
    }

    fun updateIsLauncherIconShowing(enabled: Boolean) {
        application.packageManager.setComponentEnabledSetting(
            ComponentName(application.packageName, "${CommonUtils.PACKAGE_NAME}.Home"),
            if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        _uiState.value = _uiState.value.copy(isLauncherIconShowing = enabled)
    }

}