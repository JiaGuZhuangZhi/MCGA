package com.gustate.mcga.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit

class XposedRepo(private val context: Context) {

    @SuppressLint("WorldReadableFiles")
    private val prefs =
        try {
            context.getSharedPreferences("xposed_prefs", Context.MODE_WORLD_READABLE)
        } catch (_: SecurityException) {
            null
        }

    fun isModuleActive(): Boolean = prefs != null

    fun getBoolean(key: String, def: Boolean = false): Boolean =
        prefs?.getBoolean(key, def) ?: def

    fun setBoolean(key: String, value: Boolean) {
        prefs?.edit { putBoolean(key, value) }
    }

    fun getFloat(key: String, def: Float = 0f): Float =
        prefs?.getFloat(key, def) ?: def

    fun setFloat(key: String, value: Float) {
        prefs?.edit { putFloat(key, value) }
    }

    fun getInt(key: String, def: Int = 0): Int =
        prefs?.getInt(key, def) ?: def

    fun setInt(key: String, value: Int) {
        prefs?.edit { putInt(key, value) }
    }

}