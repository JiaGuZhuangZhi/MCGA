package com.gustate.mcga.data.model

import androidx.annotation.StringRes

data class RootManager(
    @get:StringRes
    val rootManagerName: Int,
    val rootManagerVer: String
)
