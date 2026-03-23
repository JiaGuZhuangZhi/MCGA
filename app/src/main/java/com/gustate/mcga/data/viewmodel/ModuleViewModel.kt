package com.gustate.mcga.data.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch

class ModuleViewModel(context: Application) : AndroidViewModel(application = context) {

    private val _repo = XposedRepo(context)

    private val _uiState = mutableStateOf(
        value = ModuleUiState(
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
    val uiState: MutableState<ModuleUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isModuleActive = _repo.isModuleActive(),
                isRootAvailable = RootUtils.isRootAvailable(),
                rootManagerInfo = RootUtils.getRootManager(),
                isLogEnabled = _repo.getBoolean(ModuleKeys.ENABLE_LOG, true),
                isLauncherIconShowing = context.packageManager.getComponentEnabledSetting(
                    ComponentName(
                        context.packageName,
                        "${CommonUtils.PACKAGE_NAME}.Home"
                    )
                ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            )
        }
    }

    fun updateIsLogEnabled(enabled: Boolean) {
        _repo.setBoolean("log_enabled", enabled)
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