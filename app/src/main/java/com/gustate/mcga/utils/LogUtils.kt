package com.gustate.mcga.utils

import de.robv.android.xposed.XposedBridge

object LogUtils {
    fun log(message: String, tag: String, throwable: Throwable) {
        val fullMessage = "[MCGA-$tag] $message " +
                "\n throwable: " +
                "\n ${throwable.message}"
        XposedBridge.log(fullMessage)
        throw throwable
    }

    fun log(tag: String, message: String) {
        val fullMessage = "[MCGA-$tag] $message"
        XposedBridge.log(fullMessage)
    }
}