package com.gustate.mcga.utils

import android.util.Log
import com.gustate.mcga.data.keys.ModuleKeys
import io.github.libxposed.api.XposedModule

object LogUtils {

    /**
     * 输出详细日志 (可关闭)
     * @param module 模块入口类
     * @param priority [Log] 等级
     * @param tag log 标签
     * @param message log 信息
     */
    fun log(
        module: XposedModule,
        priority: Int = Log.DEBUG,
        tag: String = "未设置",
        message: String
    ) {
        val logEnabled = module
            .getRemotePreferences("mcga_prefs")
            .getBoolean(ModuleKeys.ENABLE_LOG, true)
        if (!logEnabled) return
        module.log(priority, "MCGA-$tag", message)
    }

    /**
     * 输出重要日志
     * @param module 模块入口类
     * @param priority [Log] 等级
     * @param tag log 标签
     * @param message log 信息
     * @param throwable 所需输出的异常
     */
    fun log(
        module: XposedModule,
        priority: Int = Log.DEBUG,
        tag: String = "未设置",
        message: String,
        throwable: Throwable
    ) {
        module.log(priority, "MCGA-$tag", message, throwable)
    }

}