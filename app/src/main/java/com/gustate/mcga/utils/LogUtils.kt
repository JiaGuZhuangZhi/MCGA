package com.gustate.mcga.utils

import com.gustate.mcga.data.keys.ModuleKeys
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge

object LogUtils {
    private val logEnabled by lazy {
        val prefs = XSharedPreferences(
            "com.gustate.mcga",
            "xposed_prefs"
        )
        prefs.makeWorldReadable()
        prefs.getBoolean(ModuleKeys.ENABLE_LOG, true)
    }

    fun log(message: String, tag: String, throwable: Throwable) {
        val fullMessage = "[MCGA-$tag] $message " +
                "\n throwable: " +
                "\n ${throwable.message}"
        XposedBridge.log(fullMessage)
        throw throwable
    }

    fun log(tag: String, message: String) {
        if (!logEnabled) return
        val fullMessage = "[MCGA-$tag] $message"
        XposedBridge.log(fullMessage)
    }
}