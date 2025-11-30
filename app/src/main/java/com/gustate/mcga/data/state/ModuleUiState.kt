package com.gustate.mcga.data.state

import com.gustate.mcga.R
import com.gustate.mcga.data.model.RootManager

data class ModuleUiState(
    val isModuleActive: Boolean = false,
    val isRootAvailable: Boolean = false,
    val rootManagerInfo: RootManager = RootManager(
        rootManagerName = R.string.kernelsu,
        rootManagerVer = "100"
    )
)