package com.gustate.mcga.utils

import android.util.Log
import com.gustate.mcga.data.keys.ModuleKeys
import io.github.libxposed.api.XposedModule

object LogUtils {

    /**
     * 输出 Log
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

}