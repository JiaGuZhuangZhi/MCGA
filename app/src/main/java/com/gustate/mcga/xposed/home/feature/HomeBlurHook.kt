/**
 * MCGA (Make Color Great Again) - A Free and Open-Source Xposed Module for ColorOS Users
 *
 * Copyright (C) 2026 Zhuangzhi Meng (Gustate XiaoMeng)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gustate.mcga.xposed.home.feature

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gustate.mcga.data.setting.home.group.DockConfig
import com.gustate.mcga.data.setting.home.group.HomeBlurConfig
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.isDarkMode
import com.gustate.mcga.xposed.base.HookContext
import com.gustate.mcga.xposed.base.HookFeature
import com.gustate.mcga.xposed.helper.ClassHelper.callStaticMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAndHookMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyConstructor
import com.gustate.mcga.xposed.helper.ClassHelper.setAnyField
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.home.HomeHook.HOME_BLUR

class HomeBlurHook(
    private val hookContext: HookContext
) : HookFeature<HomeBlurConfig> {

    // 当前运行时配置
    @Volatile
    private var config: HomeBlurConfig? = null

    // 是否已经安装 Hook
    @Volatile
    private var isInstalled = false

    // Launcher 主线程 Handler
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 安装桌面 Dock 栏 Hook
     * @param config 初始配置文件
     */
    override fun install(config: HomeBlurConfig) {
        // 已安装不再重复安装
        if (isInstalled) {
            update(config)
            return
        }
        // 加载配置
        this.config = config
        // 分版本进行安装
        val osVer = RootUtils.getColorOSVersion()
        if (osVer.startsWith(prefix = "V17.0"))
            installHomeBlurOS170()
        else log(
            module = hookContext.module, tag = HOME_BLUR, priority = Log.ERROR,
            message = "❌ 安装桌面高斯模糊修改失败, 当前版本未适配该功能"
        )
        // 修改安装状态
        isInstalled = true
        // 输出 log
        log(
            module = hookContext.module, tag = HOME_BLUR,
            message = "✅ 安装桌面高斯模糊修改成功"
        )
    }

    /**
     * 更新桌面 Dock 栏运行时配置
     * @param config 最新配置
     */
    override fun update(config: HomeBlurConfig) {
        mainHandler.post {

        }
    }

    private fun installHomeBlurOS170() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "d",
                className = "nr.a",
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    "java.lang.Integer",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig.enabled
                    if (currentConfig.enabled != true)
                        return@intercept result
                    val context = ContextHelper
                        .getContext(classLoader = hookContext.classLoader)
                    val isDarkMode = context.isDarkMode()
                    val blurCustomRadius =
                        if (isDarkMode) currentConfig.dark.radius
                        else currentConfig.light.radius
                    val blendCustomColor =
                        if (isDarkMode) currentConfig.dark.blendColor
                        else currentConfig.light.blendColor
                    val mixedCustomColor =
                        if (isDarkMode) currentConfig.dark.mixedColor
                        else currentConfig.light.mixedColor
                    val throttleMs = chain.args[0] as Int
                    val blendColor = blendCustomColor
                        ?: chain.args[1] as Int
                    val mixedColor = mixedCustomColor
                        ?: chain.args[2] as Int
                    val blurRadius = blurCustomRadius
                        ?: 120f

                    fun Int.toColorArray() = floatArrayOf(
                        Color.red(this) / 255f,
                        Color.green(this) / 255f,
                        Color.blue(this) / 255f,
                        Color.alpha(this) / 255f
                    )

                    val blendColorArray = blendColor.toColorArray()
                    val mixColorArray = mixedColor.toColorArray()
                    val oplusGlobalBlurEffectParamInstance =
                        getAnyConstructor(
                            className = "com.oplus.graphics." +
                                    "OplusGlobalBlurEffectParam",
                            classLoader = hookContext.classLoader
                        ).newInstance()
                    mapOf(
                        "blurRadius" to blurRadius,
                        "blendMode" to 0,
                        "blendColor0" to blendColorArray,
                        "blendColor1" to mixColorArray,
                        "blurThrottleMs" to throttleMs
                    ).forEach { (field, value) ->
                        oplusGlobalBlurEffectParamInstance
                            .setAnyField(
                                name = field,
                                value = value
                            )
                    }
                    callStaticMethod<Any>(
                        name = "setEffectParam",
                        className = "com.oplus.graphics.OplusGlobalEffect",
                        classLoader = hookContext.classLoader,
                        params = arrayOf(
                            oplusGlobalBlurEffectParamInstance
                        ),
                        paramTypes = arrayOf(
                            "com.oplus.graphics.OplusGlobalEffectParam"
                        )
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_BLUR,
                        message = "❌ 修改桌面模糊参数失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = HOME_BLUR,
                message = "❌ 修改桌面模糊参数失败",
                throwable = e
            )
        }
    }


    /**
     * 获取当前配置
     * @return [DockConfig] Dock 配置
     */
    private fun getConfig(): HomeBlurConfig {
        return config
            ?: throw NoSuchFieldException(
                "❌ Hook 层无法读取系统桌面高斯模糊配置"
            )
    }
}