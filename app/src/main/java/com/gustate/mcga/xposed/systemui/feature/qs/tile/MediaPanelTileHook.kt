package com.gustate.mcga.xposed.systemui.feature.qs.tile

import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * 控制中心媒体磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class MediaPanelTileHook {

    /**
     * 修改控制中心媒体磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun modifyCornerRadius(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {

    }

    /**
     * 修改控制中心媒体磁贴圆角半径
     * 适配 ColorOS V16.1.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun modifyCornerRadiusOS161(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {

    }

    /**
     * 修改控制中心媒体磁贴圆角半径
     * 适配 ColorOS V16.0.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 媒体磁贴圆角半径 (dp)
     */
    fun modifyCornerRadiusOS160(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {

    }

}