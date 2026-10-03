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

package com.gustate.mcga.xposed.systemui.feature.qs.tile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.gustate.mcga.data.setting.systemui.tile.TwoXOneConfig
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.base.HookContext
import com.gustate.mcga.xposed.base.HookFeature
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.callConstructor
import com.gustate.mcga.xposed.helper.ClassHelper.callStaticMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAndHookMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ContextHelper
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_2X1_LOG
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * 控制中心 2*1 磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class TwoXOneTileHook(
    private val hookContext: HookContext
) : HookFeature<TwoXOneConfig> {

    // 当前运行时配置
    @Volatile
    private var config: TwoXOneConfig? = null

    // 是否已经安装 Hook
    @Volatile
    private var isInstalled = false

    /**
     * 已创建的 2*1 磁贴 View。
     * 用于配置变化后主动刷新已经存在的 View
     */
    private val tileViews = mutableListOf<WeakReference<View>>()

    // 2*1 磁贴 View 类
    private var qsTileViewTwoXOneClass: Class<*>? = null

    // 控制中心与通知中心分离模式下资源池
    private var sepQSResPoolClass: Class<*>? = null

    // 控制中心与通知中心合并模式下资源池
    private var stdQSResPoolClass: Class<*>? = null

    // hook 并不区分用户使用何种控制中心模式
    val qsResPoolClasses
        get() = listOf(
            // 控制中心与通知中心分离模式
            sepQSResPoolClass,
            // 控制中心与通知中心合并模式
            stdQSResPoolClass
        )


    private var cornerOutlineProviderClass: Class<*>? = null

    private var qsTileBkgOutlineDefault: Any? = null

    // SystemUI 主线程 Handler
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 安装 Hook
     * @param config 初始配置文件
     */
    override fun install(config: TwoXOneConfig) {
        // 已安装不再重复安装
        if (isInstalled) {
            update(config)
            return
        }
        // 安装调用收集
        installTwoXOneTileTracking()
        // 加载配置
        this.config = config
        // 分版本进行安装
        val osVer = RootUtils.getColorOSVersion()
        when {
            listOf("V17.0", "V16.1").any {
                osVer.startsWith(prefix = it)
            } -> installOS161And170()

            osVer.startsWith(prefix = "V16.0") ->
                installOS160()

            else ->
                return log(
                    module = hookContext.module,
                    tag = QS_TILE_2X1_LOG, priority = Log.ERROR,
                    message = "❌ 安装 2*1 磁贴 Hook 失败, 该版本尚未适配此功能"
                )
        }
        // 修改安装状态
        isInstalled = true
        // 输出 log
        log(
            module = hookContext.module, tag = QS_TILE_2X1_LOG,
            message = "✅ 2*1 磁贴 Hook 安装完成"
        )
    }

    /**
     * 更新 2*1 磁贴运行时配置
     * @param config 最新配置
     */
    override fun update(config: TwoXOneConfig) {
        mainHandler.post {
            // 更新配置
            this.config = config
            val osVer = RootUtils.getColorOSVersion()
            when {
                listOf("V17.0", "V16.1").any {
                    osVer.startsWith(prefix = it)
                } -> updateOS161And170()

                osVer.startsWith(prefix = "V16.0") ->
                    updateOS160()

                else ->
                    return@post log(
                        module = hookContext.module,
                        tag = QS_TILE_2X1_LOG, priority = Log.ERROR,
                        message = "❌ 热重载 2*1 磁贴 Hook 失败, 该版本尚未适配此功能"
                    )
            }
        }
    }

    /**
     * 获取当前配置
     */
    private fun getConfig(): TwoXOneConfig? = config

    /**
     * 安装 ColorOS 16.1 / 17 的 Hook
     */
    private fun installOS161And170() {
        prepareInstall()
        prepareInstallOS161And170()
        installCornerRadiusOS161And170()
        installTileStateFullBkgOS161And170()
        installHideTileIconBkgOS161And170()
        installTileIconSizeOS161And170()
        installTileTextColorOS160To170()
    }

    /**
     * 安装 ColorOS 16.0 的 Hook
     */
    private fun installOS160() {
        prepareInstall()
        installCornerRadiusOS160()
        installTileStateFullBkgOS160()
        installHideTileIconBkgOS160()
        installTileIconSizeOS160()
        installTileTextColorOS160To170()
    }

    private fun prepareInstall() {
        qsTileViewTwoXOneClass = loadClass(
            name = "com.oplus.systemui.plugins.qs.customize.view.tile." +
                    "OplusQSResizeableTileViewTwoXOne",
            loader = hookContext.classLoader
        )
    }

    private fun prepareInstallOS161And170() {
        sepQSResPoolClass = loadClass(
            name = "com.oplus.systemui.qs.base.res.SepQSResPool",
            loader = hookContext.classLoader
        )
        stdQSResPoolClass = loadClass(
            name = "com.oplus.systemui.qs.base.res.StdQSResPool",
            loader = hookContext.classLoader
        )
        cornerOutlineProviderClass = loadClass(
            name = "com.oplusos.systemui.common.outline.CornerOutlineProvider",
            loader = hookContext.classLoader
        )
    }

    /**
     * 修改控制中心 2*1 磁贴圆角半径
     * 适配 ColorOS V16.1.0 & ColorOS 17.0.0
     * 刷新时请调用 SepQSResPool 与 StdQSResPool
     * updateHighLightTileOutline 函数
     */
    private fun installCornerRadiusOS161And170() {
        try {
            val qsConstantClass = loadClass(
                name = "com.oplus.systemui.qs.base.res.util.QSConstant",
                loader = hookContext.classLoader
            )
            qsResPoolClasses.forEach { qsResPoolClass ->
                qsResPoolClass.getAndHookMethod(
                    module = hookContext.module,
                    name = "updateHighLightTileOutline",
                    paramTypes = arrayOf(cornerOutlineProviderClass),
                    classLoader = hookContext.classLoader
                ).intercept { chain ->
                    // 完成当前函数运算并缓存结果
                    val result = chain.proceed()
                    try {
                        // 当默认值不存在时存储默认值
                        if (qsTileBkgOutlineDefault == null)
                            qsTileBkgOutlineDefault = chain.args[0]
                        // 读取当前配置信息
                        val currentConfig = getConfig()
                        val isEnable = currentConfig
                            ?.enableCustomQsResizeableTile
                            ?: false
                        val cornerRadiusDp = currentConfig
                            ?.qsResizeableTileCornerRadius
                            ?: return@intercept result

                        // 功能关闭放行原始结果
                        if (!isEnable) return@intercept result

                        // 替换自定义的圆角
                        val context = ContextHelper
                            .getContext(classLoader = hookContext.classLoader)
                        val cornerRadiusPx = cornerRadiusDp.dpToPx(context = context)
                        val qsTileBkgOutline = qsConstantClass.callStaticMethod<Any>(
                            name = "getSmoothRoundRectOutlineProvider",
                            params = arrayOf(context, cornerRadiusPx),
                            paramTypes = arrayOf(
                                Context::class.java,
                                Float::class.javaPrimitiveType
                            ),
                            classLoader = hookContext.classLoader
                        )
                        log(
                            module = hookContext.module, tag = QS_TILE_2X1_LOG,
                            message = "✅ 成功修改 2*1 磁贴圆角半径为 $cornerRadiusDp dp"
                        )
                        // 将替换完成的 provider 塞进参数并执行
                        chain.proceed(
                            arrayOf(
                                qsTileBkgOutline
                            )
                        )
                    } catch (e: Exception) {
                        log(
                            module = hookContext.module, tag = QS_TILE_2X1_LOG,
                            message = "❌ 修改 2*1 磁贴圆角半径失败",
                            throwable = e
                        )
                        return@intercept result
                    }
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改 2*1 磁贴圆角半径失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴圆角半径
     * 适配 ColorOS V16.0.0
     */
    private fun installCornerRadiusOS160() {
        try {
            qsTileViewTwoXOneClass.getAndHookMethod(
                module = hookContext.module,
                name = "getViewRadius",
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                // 完成当前函数运算并缓存结果
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    val cornerRadiusDp = currentConfig
                        ?.qsResizeableTileCornerRadius

                    // 功能关闭放行原始结果
                    if (!isEnable || cornerRadiusDp == null)
                        return@intercept result

                    // 替换自定义的圆角
                    val view = chain.thisObject as View
                    val cornerRadiusPx = cornerRadiusDp.dpToPx(view.context)
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 成功修改 2*1 磁贴圆角半径为 $cornerRadiusDp dp"
                    )
                    return@intercept cornerRadiusPx
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 修改 2*1 磁贴圆角半径失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改 2*1 磁贴圆角半径失败",
                throwable = e
            )
        }
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴
     * 适配 ColorOS V16.1.0 & ColorOS 17.0.0
     * 通过将 2*1 磁贴的 Drawable 替换为 1*1 的以实现
     */
    private fun installTileStateFullBkgOS161And170() {
        try {
            sepQSResPoolClass.getAndHookMethod(
                module = hookContext.module,
                name = "getHighlightTileViewDrawable",
                classLoader = hookContext.classLoader
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        ?.qsTwoXOneTileFillStateFullBkg
                        ?: false
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    // 功能关闭放行原始结果
                    if (!isEnable || !isParentEnable)
                        return@intercept result
                    // 使用普通磁铁的 Drawable
                    return@intercept chain.thisObject.callAnyMethod(
                        name = "getTileViewDrawable",
                        classLoader = hookContext.classLoader
                    )
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 使磁贴状态填满控制中心 2*1 磁贴失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "✅ 使磁贴状态填满控制中心 2*1 磁贴成功"
            )
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 使磁贴状态填满控制中心 2*1 磁贴失败",
                throwable = e
            )
        }
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴
     * 适配 ColorOS V16.0.0
     * 通过禁用控制中心分离模式实现
     * 感谢 Coolapk [@tikaliu](https://www.coolapk.com/u/36684465)
     */
    private fun installTileStateFullBkgOS160() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "isNeedUseSeparateDarkThemeColor",
                className = "com.oplus.systemui.qs.base.util.QsColorUtil",
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    Context::class.java,
                    Boolean::class.javaPrimitiveType
                )
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        ?.qsTwoXOneTileFillStateFullBkg
                        ?: false
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    // 功能关闭放行原始结果
                    if (!isEnable || !isParentEnable)
                        return@intercept result
                    // 获取调用栈
                    // 看看是不是 HighlightTileView 代理类在调我们
                    val stack = Throwable().stackTrace
                    val isFromProxy = stack.any {
                        it.className.contains(
                            other = "QsHighlightTileViewBackgroundProxyImpl"
                        )
                    }
                    if (isFromProxy) {
                        log(
                            module = hookContext.module, tag = QS_TILE_2X1_LOG,
                            message = "✅ 成功使磁贴状态填满 2*1 磁贴"
                        )
                        return@intercept false
                    } else return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 使磁贴状态填满控制中心 2*1 磁贴失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 使磁贴状态填满控制中心 2*1 磁贴失败",
                throwable = e
            )
        }
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * 适配 ColorOS V16.1.0 & ColorOS 17.0.0
     * 通过调包 Drawable 为空白以实现
     */
    private fun installHideTileIconBkgOS161And170() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "onIconDrawableUpdate",
                className = "com.oplus.systemui.plugins.qs.customize." +
                        "view.tile.OplusQSIconView",
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    Boolean::class.javaPrimitiveType
                )
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        ?.qsTwoXOneTileHideIconBkg
                        ?: false
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    val isInTwoXOneTile =
                        isInTwoXOneTile(chain.thisObject as? View)
                    // 功能关闭放行原始结果
                    if (!isEnable || !isParentEnable || !isInTwoXOneTile)
                        return@intercept result
                    val thisObject = chain.thisObject
                    val isTheme = chain.args[0] as? Boolean ?: false
                    val currentDrawable = if (isTheme) {
                        thisObject.callAnyMethod<Any>(
                            name = "getThemeDrawable",
                            classLoader = hookContext.classLoader
                        )
                    } else {
                        thisObject.callAnyMethod(
                            name = "getBgDrawable",
                            classLoader = hookContext.classLoader
                        )
                    }
                    currentDrawable.callAnyMethod<Any>(
                        name = "setDrawable",
                        params = arrayOf(Color.TRANSPARENT.toDrawable()),
                        paramTypes = arrayOf(Drawable::class.java),
                        classLoader = hookContext.classLoader
                    )
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态) 成功"
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                throwable = e
            )
        }
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * 适配 ColorOS V16.0.0
     * 通过调包 Drawable 为空白以实现
     */
    private fun installHideTileIconBkgOS160() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "setBackground",
                className = "com.oplus.systemui.plugins.qs.customize." +
                        "view.tile.OplusQSIconView",
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(Drawable::class.java)
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isEnable = currentConfig
                        ?.qsTwoXOneTileHideIconBkg
                        ?: false
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    val isInTwoXOneTile = isInTwoXOneTile(
                        chain.thisObject as? View
                    )
                    // 功能关闭或不是 2*1 Tile放行原始结果
                    if (!isEnable || !isParentEnable || !isInTwoXOneTile)
                        return@intercept result
                    // 替换颜色参数
                    val args = chain.args.toMutableList()
                    args[0] = Color.TRANSPARENT.toDrawable()
                    val newArgs = args.toTypedArray()
                    val newResult = chain
                        .proceed(newArgs)
                    return@intercept newResult
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态) 成功"
            )
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴图标大小
     * 适配 ColorOS V16.1.0 & ColorOS V17.0.0
     * 通过在 onMeasure 时调整 size 以实现
     */
    private fun installTileIconSizeOS161And170() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "onMeasure",
                className = "com.oplus.systemui.plugins.qs.customize." +
                        "view.tile.OplusQSIconView",
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
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    val iconSizeDp = currentConfig
                        ?.qsTwoXOneTileIconSize
                    val isEnable = iconSizeDp != null
                    val isInTwoXOneTile =
                        isInTwoXOneTile(chain.thisObject as? View)
                    // 功能关闭或不是 2*1 Tile放行原始结果
                    if (!isEnable || !isParentEnable || !isInTwoXOneTile)
                        return@intercept result
                    // 通过 setMeasuredDimension 修改大小
                    val rootView = chain.thisObject as View
                    val context = rootView.context
                    val sizePx = iconSizeDp
                        .dpToPx(context)
                        .roundToInt()
                    rootView.callAnyMethod<ImageView>(
                        name = "getIconView",
                        classLoader = hookContext.classLoader
                    ).callAnyMethod<Any>(
                        name = "setMeasuredDimension",
                        params = arrayOf(
                            sizePx,
                            sizePx
                        ),
                        paramTypes = arrayOf(
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType
                        ),
                        classLoader = hookContext.classLoader
                    )
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 成功修改 2*1 磁贴图标大小为 ${iconSizeDp}dp"
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 修改控制中心 2*1 磁贴图标大小失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改控制中心 2*1 磁贴图标大小失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴图标大小
     * 适配 ColorOS V16.0.0
     */
    private fun installTileIconSizeOS160() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "getIconSize",
                className = "com.oplus.systemui.plugins.qs.customize." +
                        "view.tile.OplusQSIconView",
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    "com.oplusos.systemui.common.model.SpanSize",
                    Boolean::class.javaPrimitiveType
                )
            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    val iconSizeDp = currentConfig
                        ?.qsTwoXOneTileIconSize
                    val isEnable = iconSizeDp != null
                    val isInTwoXOneTile =
                        isInTwoXOneTile(chain.thisObject as? View)
                    // 功能关闭或不是 2*1 Tile放行原始结果
                    if (!isEnable || !isParentEnable || !isInTwoXOneTile)
                        return@intercept result
                    // 通过修改 getIconSize 修改大小
                    val view = chain.thisObject as View
                    val iconSizePx = iconSizeDp
                        .dpToPx(context = view.context)
                        .roundToInt()
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 成功修改 2*1 磁贴图标大小为 ${iconSizeDp}dp"
                    )
                    return@intercept iconSizePx
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 修改控制中心 2*1 磁贴图标大小失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改控制中心 2*1 磁贴图标大小失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴标签字体颜色
     * 适配 ColorOS 16~17
     * 直接改, 不解释
     */
    fun installTileTextColorOS160To170() {
        try {
            getAndHookMethod(
                module = hookContext.module,
                name = "getColorByTileState",
                className = "com.oplus.systemui.plugins.qs.customize." +
                        "view.tile.OplusQSHighlightTileViewLabelColorManager",
                classLoader = hookContext.classLoader,
                paramTypes = arrayOf(
                    Context::class.java,
                    $$"com.android.systemui.plugins.qs.QSTile$State"
                )

            ).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 读取当前配置信息
                    val currentConfig = getConfig()
                    val isParentEnable = currentConfig
                        ?.enableCustomQsResizeableTile
                        ?: false
                    val activeTitleColor = currentConfig
                        ?.qsTwoXOneTileActiveTitleColor
                        ?: return@intercept result
                    val activeDesColor = currentConfig
                        .qsTwoXOneTileActiveDesColor
                        ?: return@intercept result
                    val inactiveTitleColor = currentConfig
                        .qsTwoXOneTileInactiveTitleColor
                        ?: return@intercept result
                    val inactiveDesColor = currentConfig
                        .qsTwoXOneTileInactiveDesColor
                        ?: return@intercept result
                    // 功能关闭或不是 2*1 Tile放行原始结果
                    if (!isParentEnable)
                        return@intercept result
                    val stateObj = chain.args[1]
                    val state = stateObj.getAnyField<Int>(name = "state")
                    // 依据 state 获取颜色
                    val (titleColor, desColor) = when (state) {
                        // Active
                        2 -> activeTitleColor to activeDesColor
                        // Inactive / Unavailable
                        else -> inactiveTitleColor to inactiveDesColor
                    }
                    // 反射构造 kotlin.Pair(ColorStateList, ColorStateList)
                    val resultPair = callConstructor<Any>(
                        className = "kotlin.Pair",
                        params = arrayOf(
                            ColorStateList.valueOf(titleColor),
                            ColorStateList.valueOf(desColor)
                        ),
                        paramTypes = arrayOf(
                            Any::class.java,
                            Any::class.java
                        ),
                        classLoader = hookContext.classLoader
                    )
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 成功修改 2*1 磁贴文本颜色 (state: $state)"
                    )
                    return@intercept resultPair
                } catch (e: Exception) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 修改控制中心 2*1 磁贴标签字体颜色失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改控制中心 2*1 磁贴标签字体颜色失败",
                throwable = e
            )
        }
    }

    /**
     * View 的父级链里是否有 2*1 磁贴
     * @param view 应为 OplusQSIconView
     */
    private fun isInTwoXOneTile(
        view: View?
    ): Boolean {
        var p = view?.parent
        while (p != null) {
            if (p.javaClass.name.contains(other = "OplusQSResizeableTileViewTwoXOne"))
                return true
            p = p.parent
        }
        return false
    }

    /**
     * 收集已经创建的 OplusQSResizeableTileViewTwoXOne
     * 适配 ColorOS 16.0 ~ ColorOS 17.0
     */
    private fun installTwoXOneTileTracking() {
        try {
            val clazz = loadClass(
                name =
                    "com.oplus.systemui.plugins.qs.customize.view.tile." +
                            "OplusQSResizeableTileViewTwoXOne",
                loader = hookContext.classLoader
            )

            hookContext.module.hook(
                clazz.getAnyMethod(
                    name = "onAttachedToWindow",
                    classLoader = hookContext.classLoader
                )
            ).intercept { chain ->
                val result = chain.proceed()
                val view = chain.thisObject as? View
                    ?: return@intercept result
                // 是否为 2*1 磁贴
                if (
                    view.javaClass.name.contains(
                        other = "OplusQSResizeableTileViewTwoXOne"
                    )
                ) registerTileView(view)
                return@intercept result
            }
            hookContext.module.hook(
                clazz.getAnyMethod(
                    name = "onRecycle",
                    classLoader = hookContext.classLoader
                )
            ).intercept { chain ->
                val view = chain.thisObject as? View
                try {
                    chain.proceed()
                } finally {
                    if (
                        view?.javaClass
                            ?.name
                            ?.contains(
                                other = "OplusQSResizeableTileViewTwoXOne"
                            ) == true
                    ) unregisterTileView(view)
                }
                null
            }
        } catch (e: Throwable) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 安装 2*1 磁贴动态刷新监听失败",
                throwable = e
            )
        }
    }

    /**
     * 配置发生变化后刷新所有已经存在的 2x1 磁贴
     * 适配 ColorOS V16.1.0 & ColorOS 17.0.0
     * 在 onAttachedToWindow() 中
     * 边框: void onOutlineUpdate(List<SpanSize> list)
     * 背景: void onDrawableUpdate(List<SpanSize> list, boolean z)
     */
    private fun updateOS161And170() {
        try {
            // 更新控制中心在经典模式与分离模式下圆角值
            qsResPoolClasses.forEach { qsResPoolClass ->
                qsResPoolClass.callStaticMethod(
                    name = "updateHighLightTileOutline",
                    params = arrayOf(qsTileBkgOutlineDefault),
                    paramTypes = arrayOf(cornerOutlineProviderClass),
                    classLoader = hookContext.classLoader
                )
            }
            // 清理已被 GC 的 View
            cleanupTileViews()
            // 更新磁贴
            tileViews.forEach { reference ->
                // 未储存磁贴不进行更新
                val tileView = reference.get()
                    ?: return@forEach
                // 磁贴已被移除不进行更新
                if (!tileView.isAttachedToWindow)
                    return@forEach
                try {
                    // 获取必要参数
                    val tileViewSize = tileView.callAnyMethod<Any>(
                        name = "getTileViewSize",
                        classLoader = hookContext.classLoader
                    )
                    // 刷新边框
                    tileView.callAnyMethod<Any>(
                        name = "onOutlineUpdate",
                        params = arrayOf(listOf(tileViewSize)),
                        paramTypes = arrayOf(List::class.java),
                        classLoader = hookContext.classLoader
                    )
                    // 刷新背景
                    listOf(true, false).forEach { isTheme ->
                        tileView.callAnyMethod<Any>(
                            name = "onDrawableUpdate",
                            params = arrayOf(
                                listOf(tileViewSize),
                                isTheme
                            ),
                            paramTypes = arrayOf(
                                List::class.java,
                                Boolean::class.javaPrimitiveType
                            ),
                            classLoader = hookContext.classLoader
                        )
                    }
                    val iconView = tileView.callAnyMethod<Any>(
                        name = "getIconView",
                        classLoader = hookContext.classLoader
                    ) as? View
                    // 对 QSIconView 背景进行刷新
                    listOf(true, false).forEach { isTheme ->
                        iconView.callAnyMethod(
                            name = "onIconDrawableUpdate",
                            params = arrayOf(isTheme),
                            paramTypes = arrayOf(
                                Boolean::class.javaPrimitiveType
                            ),
                            classLoader = hookContext.classLoader
                        )
                    }
                    // 防止有宝宝不听话 (触发 onMeasure)
                    (iconView as? ViewGroup)?.let { group ->
                        for (i in 0 until group.childCount) {
                            group.getChildAt(i).apply {
                                requestLayout()
                                invalidate()
                                invalidateOutline()
                            }
                        }
                    }
                    listOf(iconView, tileView).forEach { view ->
                        view?.requestLayout()
                        view?.invalidate()
                        view?.invalidateOutline()
                    }
                } catch (e: Throwable) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 热重载 2*1 磁贴失败",
                        throwable = e
                    )
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 热重载 2*1 磁贴失败",
                throwable = e
            )
        }
    }

    /**
     * 配置发生变化后刷新所有已经存在的 2x1 磁贴
     * 适配 ColorOS V16.0.0
     * 在 onConfigurationChanged() 中
     * 背景：getBackgroundProxy().refreshViewBackground()
     */
    private fun updateOS160() {
        try {
            // 清理已被 GC 的 View
            cleanupTileViews()
            // 更新磁贴
            tileViews.forEach { reference ->
                // 未储存磁贴不进行更新
                val tileView = reference.get()
                    ?: return@forEach
                // 磁贴已被移除不进行更新
                if (!tileView.isAttachedToWindow)
                    return@forEach
                try {
                    // 刷新背景 (ColorOS 16 直接使用了 Drawable 形状)
                    tileView.callAnyMethod<Any>(
                        name = "getBackgroundProxy",
                        classLoader = hookContext.classLoader
                    ).callAnyMethod<Any>(
                        name = "refreshViewBackground",
                        classLoader = hookContext.classLoader
                    )
                    // 对 QSIconView 进行刷新
                    val iconView = tileView.callAnyMethod<Any>(
                        name = "getIconView",
                        classLoader = hookContext.classLoader
                    ) as? View
                    iconView.callAnyMethod<Any>(
                        name = "updateBgColorImmediately",
                        classLoader = hookContext.classLoader
                    )
                    iconView.callAnyMethod<Any>(
                        name = "updateIconViewSize",
                        params = arrayOf(
                            iconView.getAnyField<Float>(
                                name = "iconScaleRatio"
                            )
                        ),
                        paramTypes = arrayOf(
                            Float::class.javaPrimitiveType
                        ),
                        classLoader = hookContext.classLoader
                    )
                    // 防止有宝宝不听话 (触发 onMeasure)
                    (iconView as? ViewGroup)?.let { group ->
                        for (i in 0 until group.childCount) {
                            group.getChildAt(i).apply {
                                requestLayout()
                                invalidate()
                                invalidateOutline()
                            }
                        }
                    }
                    listOf(iconView, tileView).forEach { view ->
                        view?.requestLayout()
                        view?.invalidate()
                        view?.invalidateOutline()
                    }
                } catch (e: Throwable) {
                    log(
                        module = hookContext.module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 热重载 2*1 磁贴失败",
                        throwable = e
                    )
                }
            }
        } catch (e: Exception) {
            log(
                module = hookContext.module, tag = QS_TILE_2X1_LOG,
                message = "❌ 热重载 2*1 磁贴失败",
                throwable = e
            )
        }
    }

    /**
     * 注册磁贴 View
     * @param view 磁贴 [View] 对象
     */
    private fun registerTileView(view: View) {
        // 清理已经被 GC 的 View
        cleanupTileViews()
        // 存在不添加
        if (tileViews.any { it.get() === view }) {
            return
        }
        // 追加不存在项
        tileViews += WeakReference(view)
    }

    /**
     * 移除磁贴 View
     * @param view 磁贴 [View] 对象
     */
    private fun unregisterTileView(view: View) {
        tileViews.removeAll {
            val target = it.get()
            target == null || target === view
        }
    }

    /**
     * 清理已经被 GC 的 View
     */
    private fun cleanupTileViews() {
        tileViews.removeAll {
            it.get() == null
        }
    }
}