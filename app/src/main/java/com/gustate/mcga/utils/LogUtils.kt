package com.gustate.mcga.utils

import android.content.Context
import android.util.Log
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.ModuleKeys
import io.github.libxposed.api.XposedModule

object LogUtils {

    private lateinit var xposedRepo: XposedRepo

    fun init(context: Context) {
        xposedRepo = XposedRepo
            .getInstance(context = context.applicationContext)
    }

    private val logEnabled by lazy {
        xposedRepo
            .getBoolean(key = ModuleKeys.ENABLE_LOG)
    }

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
        if (!logEnabled) return
        module.log(priority, "MCGA-$tag", message)
    }

}