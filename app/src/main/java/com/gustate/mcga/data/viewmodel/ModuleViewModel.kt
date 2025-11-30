package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gustate.mcga.R
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.model.RootManager
import com.gustate.mcga.data.state.ModuleUiState
import com.gustate.mcga.utils.RootUtils
import kotlinx.coroutines.launch

class ModuleViewModel(context: Application) : AndroidViewModel(application = context) {

    private val _repo = XposedRepo(context)

    private val _uiState = mutableStateOf(
        value = ModuleUiState(
            isModuleActive = false,
            isRootAvailable = false,
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
                rootManagerInfo = RootUtils.getRootManager()
            )
        }
    }

}