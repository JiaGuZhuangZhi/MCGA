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

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isNotEmpty
import com.gustate.mcga.data.setting.home.group.DockConfig
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.base.HookContext
import com.gustate.mcga.xposed.base.HookFeature
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAndHookMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.getStaticField
import com.gustate.mcga.xposed.helper.ClassHelper.setAnyField
import com.gustate.mcga.xposed.home.HomeHook.HOME_DOCK

class HomeDockHook(
    private val hookContext: HookContext
) : HookFeature<DockConfig> {

    companion object {
        const val TARGET_CLASS_NAME = "com.android.launcher3.OplusHotseat"
    }

    // 当前运行时配置
    @Volatile
    private var config: DockConfig? = null

    // 是否已经安装 Hook
    @Volatile
    private var isInstalled = false

    // Launcher 主线程 Handler
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 安装桌面 Dock 栏 Hook
     * @param config 初始配置文件
     */
    override fun install(config: DockConfig) {
        // 已安装不再重复安装
        if (isInstalled) {
            update(config)
            return
        }
        // 加载配置
        this.config = config
        // 分版本进行安装
        val osVer = RootUtils.getColorOSVersion()
        when {
            osVer.startsWith(prefix = "V17.0") ->
                installOS170()

            osVer.startsWith(prefix = "V16.1") ->
                installOS161()

            osVer.startsWith(prefix = "V16.0") ->
                installOS160()

            else -> log(
                module = hookContext.module, tag = HOME_DOCK, priority = Log.ERROR,
                message = "❌ 安装桌面 Dock 栏 Hook 失败, 当前版本未适配该功能"
            )
        }
        // 修改安装状态
        isInstalled = true
        // 输出 log
        log(
            module = hookContext.module, tag = HOME_DOCK,
            message = "✅ 安装桌面 Dock 栏 Hook 成功"
        )
    }

    /**
     * 更新桌面 Dock 栏运行时配置
     * @param config 最新配置
     */
    override fun update(config: DockConfig) {
        mainHandler.post {

        }
    }

    fun installOS170() {
        installShowDockBkgOS170()
        installHotseatBkgPaddingAndMarginOS170()
        installModifyHotseatBkgOS170()
    }

    fun installOS161() {

    }

    fun installOS160() {

    }

    private fun installShowDockBkgOS170() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "hasLargeDisplayFeatures",
                className = "com.android.common.util.ScreenUtils",
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        .enableDockBkg
                        ?: false
                    // 功能关闭或不是 Hotseat 放行原始结果
                    if (!isEnable || !isCalledFromOplusHotseat())
                        return@intercept result
                    // 直接返回 true
                    return@intercept true
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 强制显示桌面 Dock 栏失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            getAndHookMethod(
                module = hookContext.module,
                name = "onAttachedToWindow",
                className = TARGET_CLASS_NAME,
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        .enableDockBkg
                        ?: false
                    // 功能关闭放行原始结果
                    if (!isEnable) return@intercept result
                    // 按其原有逻辑补充调用 setDockerBackground
                    val hotseatView = chain.thisObject as View
                    val hotseatVto = hotseatView.viewTreeObserver
                    hotseatVto.addOnPreDrawListener(
                        object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                hotseatVto
                                    .removeOnPreDrawListener(this)
                                hotseatView.setDockerBackgroundOS170()
                                return true
                            }
                        }
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 强制显示桌面 Dock 栏失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            getAndHookMethod(
                module = hookContext.module,
                name = "onMeasure",
                className = TARGET_CLASS_NAME,
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        .enableDockBkg
                        ?: false
                    // 功能关闭放行原始结果
                    if (!isEnable) return@intercept result
                    // 按其原有逻辑补充调用 setDockerBackground
                    val hotseatView = chain.thisObject as View
                    val shortcutsAndWidgetsView = hotseatView
                        .getAnyField<ViewGroup>(
                            name = "mShortcutsAndWidgets"
                        )
                    if (shortcutsAndWidgetsView.isNotEmpty()) {
                        hotseatView.setDockerBackgroundOS170()
                    } else {
                        val dockerBkgDrawable = hotseatView
                            .getAnyField<Drawable?>(
                                name = "mDockerBackgroundDrawable"
                            )
                        if (dockerBkgDrawable != null) {
                            dockerBkgDrawable.callback = null
                        }
                        hotseatView.setAnyField(
                            name = "mDockerBackgroundDrawable",
                            value = null
                        )
                        shortcutsAndWidgetsView.background = null
                    }
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 强制显示桌面 Dock 栏失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            getAndHookMethod(
                module = hookContext.module,
                name = "onWallpaperBrightnessChanged",
                className = TARGET_CLASS_NAME,
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        .enableDockBkg
                        ?: false
                    // 功能关闭放行原始结果
                    if (!isEnable) return@intercept result
                    // 按其原有逻辑补充调用 setDockerBackground
                    val hotseatView = chain.thisObject as View
                    hotseatView.setDockerBackgroundOS170()
                    if (
                        hotseatView.getAnyField<Any?>(
                            name = "mBlurProp"
                        ) != null
                    ) {
                        hotseatView.callAnyMethod<Any>(
                            name = "updateBlurParams",
                            params = arrayOf(
                                hotseatView.callAnyMethod<Int>(
                                    name = "getMeasuredHeight",
                                    classLoader = hookContext.classLoader
                                ) * hotseatView.javaClass.getStaticField<Float>(
                                    name = "BACKGROUND_SMOOTH_CORNER_RADIUS_FACTOR"
                                )
                            ),
                            paramTypes = arrayOf(
                                Float::class.javaPrimitiveType
                            ),
                            classLoader = hookContext.classLoader
                        )
                    }
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 强制显示桌面 Dock 栏失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            getAndHookMethod(
                module = hookContext.module,
                name = "onWallpaperBrightnessChanged",
                className = TARGET_CLASS_NAME,
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        .enableDockBkg
                        ?: false
                    // 功能关闭放行原始结果
                    if (!isEnable) return@intercept result
                    // 按其原有逻辑补充调用 setDockerBackground
                    val hotseatView = chain.thisObject as View
                    hotseatView.setDockerBackgroundOS170()
                    if (
                        hotseatView.getAnyField<Any?>(
                            name = "mBlurProp"
                        ) != null
                    ) {
                        hotseatView.callAnyMethod<Any>(
                            name = "updateBlurParams",
                            params = arrayOf(
                                hotseatView.callAnyMethod<Int>(
                                    name = "getMeasuredHeight",
                                    classLoader = hookContext.classLoader
                                ) * hotseatView.javaClass.getStaticField<Float>(
                                    name = "BACKGROUND_SMOOTH_CORNER_RADIUS_FACTOR"
                                )
                            ),
                            paramTypes = arrayOf(
                                Float::class.javaPrimitiveType
                            ),
                            classLoader = hookContext.classLoader
                        )
                    }
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 强制显示桌面 Dock 栏失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = HOME_DOCK,
                message = "❌ 强制显示桌面 Dock 栏失败",
                throwable = e
            )
        }
    }

    private fun View?.setDockerBackgroundOS170() {
        this.callAnyMethod<Any>(
            name = "setDockerBackground",
            classLoader = hookContext.classLoader,
        )
    }

    fun installHotseatBkgPaddingAndMarginOS170() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "updateDockerBackgroundBounds",
                className = TARGET_CLASS_NAME,
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                // 不 Replace, 原函数逻辑兜底吧
                // 实际上出错再 chain.proceed() 也行
                // 但我就是想少打几个字母你信吗
                val result = chain.proceed()
                try {
                    val hotseatView = chain.thisObject as View
                    val drawable = hotseatView.getAnyField<Drawable?>(
                        name = "mDockerBackgroundDrawable"
                    ) ?: return@intercept result
                    val shortcutsAndWidgetsView = hotseatView
                        .getAnyField<ViewGroup>(
                            name = "mShortcutsAndWidgets"
                        )
                    val width = shortcutsAndWidgetsView.width
                    val height = shortcutsAndWidgetsView.height
                    if (width <= 0 || height <= 0)
                        return@intercept result
                    val scrollX = shortcutsAndWidgetsView.scrollX
                    val scrollY = shortcutsAndWidgetsView.scrollY
                    drawable.setBounds(
                        scrollX - 24,
                        scrollY - 24,
                        width + scrollX + 24,
                        height + scrollY + 24
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 修改 Dock 栏背景内外边距失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = HOME_DOCK,
                message = "❌ 修改 Dock 栏背景内外边距失败",
                throwable = e
            )
        }
    }

    fun installModifyHotseatBkgOS170() {
        try {
            // 模糊模式下
            getAndHookMethod(
                module = hookContext.module,
                name = "updateBlurParams",
                className = TARGET_CLASS_NAME,
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    Float::class.javaPrimitiveType
                )
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val cornerRadius = currentConfig.dockCornerRadius
                        ?: return@intercept result
                    // 功能关闭放行原始结果
                    val hotseatView = chain.thisObject as View
                    val blurProp = hotseatView
                        .getAnyField<Any>(name = "mBlurProp")
                    val radiusPx = cornerRadius.dpToPx(context = hotseatView.context)
                    blurProp.callAnyMethod<Any>(
                        name = "setBlurCornerRadius",
                        params = arrayOf(
                            radiusPx,
                            false,
                            null,
                            true
                        ),
                        paramTypes = arrayOf(
                            Float::class.javaPrimitiveType,
                            Boolean::class.javaPrimitiveType,
                            "com.android.launcher3.model.data.ItemInfo",
                            Boolean::class.javaPrimitiveType
                        ),
                        classLoader = hookContext.classLoader
                    )
                    blurProp.callAnyMethod<Any>(
                        name = "setIgnorePageScrollBlurAnim",
                        params = arrayOf(true),
                        paramTypes = arrayOf(Boolean::class.javaPrimitiveType),
                        classLoader = hookContext.classLoader
                    )
                    return@intercept blurProp.callAnyMethod<Any>(
                        name = "getBlurDrawable",
                        classLoader = hookContext.classLoader
                    )
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = HOME_DOCK,
                        message = "❌ 修改 Dock 栏背景模糊参数失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            // 非模糊模式下
            /*getAndHookMethod(

            ).intercept { chain ->
                try {

                } catch (e: Exception) {

                }
            }*/
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = HOME_DOCK,
                message = "❌ 修改 Dock 栏背景模糊参数失败",
                throwable = e
            )
        }
    }

    /**
     * 获取当前配置
     * @return [DockConfig] Dock 配置
     */
    private fun getConfig(): DockConfig {
        return config
            ?: throw NoSuchFieldException(
                "❌ Hook 层无法读取系统桌面 Dock 栏配置"
            )
    }

    /**
     * 是否为 OplusHotseat 调用
     * @return [Boolean] 是否为 OplusHotseat 调用
     */
    private fun isCalledFromOplusHotseat(): Boolean {
        for (element in Thread.currentThread().stackTrace) {
            val className = element.className
            if (
                className == "com.android.launcher3.OplusHotseat" ||
                className.startsWith(prefix = "com.android.launcher3.OplusHotseat$")
            ) return true
        }
        return false
    }
}