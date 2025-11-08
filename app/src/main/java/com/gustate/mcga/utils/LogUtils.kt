package com.gustate.mcga.utils

import de.robv.android.xposed.XposedBridge

object LogUtils {
    fun log(message: String, tag: String = "null", throwable: Throwable? = null) {
        val fullMessage = "[$tag] $message"
        if (throwable != null) XposedBridge.log(throwable)
        else XposedBridge.log(fullMessage)
    }
}