package com.gustate.mcga.data.state

import com.gustate.mcga.R
import com.gustate.mcga.data.model.RootManager

data class ModuleUiState(
    val isReady: Boolean = false,
    val isModuleActive: Boolean = false,
    val isRootAvailable: Boolean = false,
    val isLogEnabled: Boolean = true,
    val isLauncherIconShowing: Boolean = true,
    val rootManagerInfo: RootManager = RootManager(
        rootManagerName = R.string.kernelsu,
        rootManagerVer = "100"
    )
)