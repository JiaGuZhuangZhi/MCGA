package com.gustate.mcga.xposed.base

import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 在 Hook 中稳定的上下文
 * @property module 当前 [XposedModule] 实例
 * @property param 软件包加载参数
 * @property classLoader [ClassLoader] 实例
 * @see XposedModule
 * @see XposedModuleInterface
 */
data class HookContext(
    val module: XposedModule,
    val param: XposedModuleInterface.PackageReadyParam,
    val classLoader: ClassLoader
)