package com.gustate.mcga.xposed.helper

import android.content.Context
import de.robv.android.xposed.XposedHelpers

object ContextHelper {
    fun getContext(): Context {
        val activityThread = XposedHelpers.findClass("android.app.ActivityThread", null)
        val app = XposedHelpers.callStaticMethod(activityThread, "currentApplication")
        return app as Context
    }
}