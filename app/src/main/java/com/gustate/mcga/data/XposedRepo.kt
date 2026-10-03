package com.gustate.mcga.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val _isActive = MutableStateFlow(false)
    val isActive = _isActive.asStateFlow()

    init {
        // 自动连接服务
        XposedServiceHelper.registerListener(
            object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    // 把本地所有配置同步一次到远程
                    xposedPrefs = service
                        .getRemotePreferences("mcga_prefs")
                    syncAllToRemote()
                    _isActive.value = true
                    onActiveChanged?.invoke(true)
                }

                override fun onServiceDied(service: XposedService) {
                    _isActive.value = false
                    onActiveChanged?.invoke(false)
                    xposedPrefs = null
                }
            }
        )
    }

    @Deprecated("后续使用 getBooleanOrNull 并删掉 OrNull")
    fun getBoolean(key: String, def: Boolean = false): Boolean =
        prefs?.getBoolean(key, def) ?: def

    fun getBooleanOrNull(
        key: String
    ): Boolean? =
        if (prefs.contains(key))
            prefs.getBoolean(key, false)
        else null

    fun setBoolean(key: String, value: Boolean) {
        prefs?.edit { putBoolean(key, value) }
        xposedPrefs?.edit { putBoolean(key, value) } ?: Log.e("setBoolean() is error", "11")
    }

    @Deprecated("后续使用 getFloatOrNull 并删掉 OrNull")
    fun getFloat(key: String, def: Float = 0f): Float =
        prefs?.getFloat(key, def) ?: def

    fun getFloatOrNull(
        key: String
    ): Float? =
        if (prefs.contains(key))
            prefs.getFloat(key, 0f)
        else null

    fun setFloat(key: String, value: Float) {
        prefs?.edit { putFloat(key, value) }
        xposedPrefs?.edit { putFloat(key, value) }
    }

    @Deprecated("后续使用 getIntOrNull 并删掉 OrNull")
    fun getInt(key: String, def: Int = 0): Int =
        prefs?.getInt(key, def) ?: def

    fun getIntOrNull(
        key: String
    ): Int? =
        if (prefs.contains(key))
            prefs.getInt(key, 0)
        else null

    fun setInt(key: String, value: Int) {
        prefs?.edit { putInt(key, value) }
        xposedPrefs?.edit { putInt(key, value) }
    }

    fun remove(key: String) {
        prefs.edit { remove(key) }
        xposedPrefs?.edit { remove(key) }
    }

    /**
     * 同步本地全部参数至 Lsposed 仓库
     */
    private fun syncAllToRemote() {
        val remote = xposedPrefs ?: return
        val local = prefs.all
        val remoteAll = remote.all

        remote.edit {
            // 远程有、本地没有的 key
            remoteAll.keys
                .filter { it !in local }
                .forEach { remove(it) }

            // 本地有的 key：值不一样才写
            local.forEach { (k, v) ->
                if (remoteAll[k] == v) return@forEach
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