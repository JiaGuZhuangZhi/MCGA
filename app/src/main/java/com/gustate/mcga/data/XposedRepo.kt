package com.gustate.mcga.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit

class XposedRepo(private val context: Context) {

    @SuppressLint("WorldReadableFiles")
    private val prefs = context
        .getSharedPreferences("xposed_prefs", Context.MODE_WORLD_READABLE)

    fun getBoolean(key: String, def: Boolean = false): Boolean =
        prefs.getBoolean(key, def)

    fun setBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

}