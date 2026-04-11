package com.gustate.mcga.xposed.helper

import android.content.Context
import com.gustate.mcga.xposed.helper.ClassHelper.callStaticMethod
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass

object ContextHelper {

    /**
     * 取当前 Context
     * @param classLoader [ClassLoader] 实例
     */
    fun getContext(classLoader: ClassLoader): Context {
        val activityThread = loadClass(
            className = "android.app.ActivityThread",
            classLoader = classLoader
        )
        return activityThread
            .callStaticMethod<Context>(methodName = "currentApplication")
            ?: throw NullPointerException()
    }

}