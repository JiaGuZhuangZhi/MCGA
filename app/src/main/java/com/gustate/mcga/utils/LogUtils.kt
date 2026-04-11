package com.gustate.mcga.utils

import android.content.Context
import android.util.Log
import io.github.libxposed.api.XposedModule

object LogUtils {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
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
        module.log(priority, "MCGA-$tag", message)
    }

}