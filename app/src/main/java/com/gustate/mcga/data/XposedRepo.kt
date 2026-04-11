package com.gustate.mcga.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

class XposedRepo private constructor(context: Context) {

    // 唯一实例
    companion object {
        @Volatile
        private var instance: XposedRepo? = null
        fun getInstance(context: Context): XposedRepo {
            return instance ?: synchronized(lock = this) {
                instance ?: XposedRepo(context.applicationContext)
                    .also { instance = it }
            }
        }
    }

    // 本地私有存储
    private val prefs = context
        .getSharedPreferences(
            "xposed_prefs",
            Context.MODE_PRIVATE
        )

    // Lsposed 远程存储
    private var xposedPrefs: SharedPreferences? = null

    // 模块激活时的回调
    var onActiveChanged: ((Boolean) -> Unit)? = null

    init {
        // 自动连接服务
        XposedServiceHelper.registerListener(
            object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    // 把本地所有配置同步一次到远程
                    xposedPrefs = service
                        .getRemotePreferences("mcga_prefs")
                    syncAllToRemote()
                    onActiveChanged?.invoke(true)
                }

                override fun onServiceDied(service: XposedService) {
                    onActiveChanged?.invoke(false)
                    xposedPrefs = null
                }
            }
        )
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean =
        prefs?.getBoolean(key, def) ?: def

    fun setBoolean(key: String, value: Boolean) {
        prefs?.edit { putBoolean(key, value) }
        xposedPrefs?.edit { putBoolean(key, value) } ?: Log.e("setBoolean() is error", "11")
    }

    fun getFloat(key: String, def: Float = 0f): Float =
        prefs?.getFloat(key, def) ?: def

    fun setFloat(key: String, value: Float) {
        prefs?.edit { putFloat(key, value) }
        xposedPrefs?.edit { putFloat(key, value) }
    }

    fun getInt(key: String, def: Int = 0): Int =
        prefs?.getInt(key, def) ?: def

    fun setInt(key: String, value: Int) {
        prefs?.edit { putInt(key, value) }
        xposedPrefs?.edit { putInt(key, value) }
    }

    /**
     * 同步本地全部参数至 Lsposed 仓库
     */
    private fun syncAllToRemote() {
        xposedPrefs?.edit {
            prefs.all.forEach { (k, v) ->
                when (v) {
                    is Boolean -> putBoolean(k, v)
                    is Int -> putInt(k, v)
                    is Float -> putFloat(k, v)
                    is String -> putString(k, v)
                }
            }
        }
    }
}