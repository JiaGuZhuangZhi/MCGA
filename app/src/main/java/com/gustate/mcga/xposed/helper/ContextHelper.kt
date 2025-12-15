package com.gustate.mcga.xposed.helper

import android.content.Context
import com.gustate.mcga.utils.LogUtils.log
import de.robv.android.xposed.XposedHelpers

object ContextHelper {
    private const val CONTEXT_LOG_TAG = "Context"
    fun getContext(): Context {
        return try {
            val activityThread = XposedHelpers.findClass("android.app.ActivityThread", null)
            val app = XposedHelpers.callStaticMethod(activityThread, "currentApplication")
            app as Context
        } catch (e: Throwable) {
            log(
                message = "❌ 获取 Context 失败: ${e.message}",
                tag = CONTEXT_LOG_TAG,
                throwable = e
            )
            throw e
        }
    }
}