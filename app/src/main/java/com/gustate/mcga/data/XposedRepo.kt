package com.gustate.mcga.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

class XposedRepo(context: Context) {

    // 本地私有存储
    private val prefs = context
        .getSharedPreferences(
            "xposed_prefs",
            Context.MODE_PRIVATE
        )

    // Lsposed 远程服务
    private var xposedService: XposedService? = null

    var onActiveChanged: ((Boolean) -> Unit)? = null

    init {
        // 自动连接服务
        XposedServiceHelper.registerListener(
            object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    xposedService = service
                    // 把本地所有配置同步一次到远程
                    syncAllToRemote()
                    onActiveChanged?.invoke(true)
                }

                override fun onServiceDied(service: XposedService) {
                    //xposedService = null
                    //onActiveChanged?.invoke(false)
                }
            }
        )
    }

    /**
     * 模块是否激活
     * @return 模块激活状态 - [Boolean]
     */
    fun isModuleActive(): Boolean = xposedService != null

    fun getBoolean(key: String, def: Boolean = false): Boolean =
        prefs?.getBoolean(key, def) ?: def

    fun setBoolean(key: String, value: Boolean) {
        prefs?.edit { putBoolean(key, value) }
        getRemotePrefs()?.edit { putBoolean(key, value) }
    }

    fun getFloat(key: String, def: Float = 0f): Float =
        prefs?.getFloat(key, def) ?: def

    fun setFloat(key: String, value: Float) {
        prefs?.edit { putFloat(key, value) }
        getRemotePrefs()?.edit { putFloat(key, value) }
    }

    fun getInt(key: String, def: Int = 0): Int =
        prefs?.getInt(key, def) ?: def

    fun setInt(key: String, value: Int) {
        prefs?.edit { putInt(key, value) }
        getRemotePrefs()?.edit { putInt(key, value) }
    }

    /**
     * 同步本地全部参数至 Lsposed 仓库
     */
    private fun syncAllToRemote() {
        val remotePrefs = getRemotePrefs()
        remotePrefs?.edit {
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

    /**
     * 取 Lsposed 远程仓库 SP
     * @return 本地数据库 - [SharedPreferences]
     */
    private fun getRemotePrefs(): SharedPreferences? {
        val service = xposedService
        val remotePrefs = service
            ?.getRemotePreferences("mcga_prefs")
        return remotePrefs
    }

}