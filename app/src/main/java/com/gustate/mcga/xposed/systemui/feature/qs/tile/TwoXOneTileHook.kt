package com.gustate.mcga.xposed.systemui.feature.qs.tile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.RootUtils
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ResourceHelper
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_2X1_LOG
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.math.roundToInt

/**
 * 控制中心 2*1 磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class TwoXOneTileHook {

    /**
     * 修改控制中心 2*1 磁贴圆角半径
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param cornerRadiusDp 2*1 磁贴圆角半径 (dp)
     */
    fun modifyCornerRadius(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        cornerRadiusDp: Float
    ) {
        val clazz = loadClass(
            "com.oplus.systemui.plugins.qs.customize.view.tile." +
                    "OplusQSResizeableTileView",
            param.classLoader
        ) ?: return log(
            module = module, tag = QS_TILE_2X1_LOG,
            message = "❌ 未获取到 OplusQSResizeableTileView 类"
        )
        try {
            val method = clazz.getDeclaredMethod("getRadius")
            module.hook(method).intercept { chain ->
                val view = chain.thisObject as View
                val cornerRadiusPx = cornerRadiusDp.dpToPx(view.context)
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴圆角半径为 $cornerRadiusDp dp"
                )
                cornerRadiusPx
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改 2*1 磁贴圆角半径失败: ${e.message}"
            )
        }
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴 (禁用分离模式)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun modifyTileStateFullBkg(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        val osVer = RootUtils.getColorOSVersion()
        if (osVer.startsWith(prefix = "V16.1"))
            modifyTileStateFullBkgOS161(
                module = module,
                param = param
            )
        else
            modifyTileStateFullBkgOS160(
                module = module,
                param = param
            )
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴
     * 适配 ColorOS V16.1.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    private fun modifyTileStateFullBkgOS161(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        val classLoader = param.classLoader
        try {
            val clazz = loadClass(
                className = "com.oplus.systemui.qs.base.res.drawable." +
                        "MixColorTileDrawable\$TileTypeConfig",
                classLoader = classLoader
            )
            val targetMethod = clazz
                .getDeclaredMethod("getSepHighlightTypeBuilder")
            module.hook(targetMethod).intercept { chain ->
                val instance = chain.thisObject
                val result = chain.proceed()
                try {
                    // 动态查找完整逻辑的静态方法
                    var fullLogicMethod: Method? = null
                    for (method in clazz.declaredMethods) {
                        if (method.name.contains("SXzvLqH") ||
                            (Modifier.isStatic(method.modifiers) &&
                                    method.returnType.name.contains("Builder") &&
                                    method.parameterCount == 1 &&
                                    method.parameterTypes[0] == clazz)
                        ) {
                            fullLogicMethod = method
                            break
                        }
                    }
                    if (fullLogicMethod == null)
                        throw NullPointerException("❌ 找不到 TileTypeConfig 中的相关函数")
                    // 查找完整逻辑的静态方法 case 1
                    val fullMethod = fullLogicMethod
                    fullMethod.isAccessible = true
                    val fullBuilder = fullMethod.invoke(null, instance)
                    return@intercept fullBuilder
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 使磁贴状态填满控制中心 2*1 磁贴失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "✅ 使磁贴状态填满控制中心 2*1 磁贴成功"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 使磁贴状态填满控制中心 2*1 磁贴失败",
                throwable = e
            )
        }
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴 (禁用分离模式)
     * 适配 ColorOS V16.0.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    private fun modifyTileStateFullBkgOS160(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        try {
            val qsColorUtilClazz = loadClass(
                className = "com.oplus.systemui.qs.base.util.QsColorUtil",
                classLoader = param.classLoader
            )
            val isNeedUseSeparateDarkThemeColor = qsColorUtilClazz.getDeclaredMethod(
                "isNeedUseSeparateDarkThemeColor",
                Context::class.java,
                Boolean::class.javaPrimitiveType
            )
            module.hook(isNeedUseSeparateDarkThemeColor).intercept { chain ->
                val result = chain.proceed()
                try {
                    // 获取调用栈，看看是不是那个代理类在调我们
                    val stack = Throwable().stackTrace
                    val isFromProxy = stack.any {
                        it.className.contains(other = "QsHighlightTileViewBackgroundProxyImpl")
                    }
                    if (isFromProxy) {
                        log(
                            module = module, tag = QS_TILE_2X1_LOG,
                            message = "✅ 成功使磁贴状态填满 2*1 磁贴"
                        )
                        return@intercept false
                    } else return@intercept result
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 填满 2*1 磁贴失败: ${e.message}"
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 填满 2*1 磁贴失败: ${e.message}"
            )
        }
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    fun hideTileIconBkg(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        val osVer = RootUtils.getColorOSVersion()
        if (osVer.startsWith(prefix = "V16.1"))
            hideTileIconBkgOS161(
                module = module,
                param = param
            )
        else
            hideTileIconBkgOS160(
                module = module,
                param = param
            )
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * 适配 ColorOS V16.1.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    private fun hideTileIconBkgOS161(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        val classLoader = param.classLoader
        try {
            val tileClazz = loadClass(
                className = "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSResizeableTileViewTwoXOne",
                classLoader = classLoader
            )
            val tileConstructor = tileClazz.getDeclaredConstructor(
                Context::class.java,
                loadClass(
                    className = "com.android.systemui.plugins.qs.QSIconView",
                    classLoader = classLoader
                ),
                loadClass(
                    className = "com.oplus.systemui.plugins.qs.customize.view.tile.QsLabelView",
                    classLoader = classLoader
                )
            )
            module.hook(tileConstructor).intercept { chain ->
                val result = chain.proceed()
                try {
                    val rootView = chain.thisObject as ViewGroup
                    val resources = rootView.resources
                    ResourceHelper.getIdentifier(
                        res = resources,
                        pkgName = param.packageName,
                        resType = "id",
                        resName = "oplus_qs_tile_icon_bg"
                    ) { resId ->
                        val tileIconBkg = rootView.findViewById<View>(resId)
                        tileIconBkg.alpha = 0f
                        log(
                            module = module, tag = QS_TILE_2X1_LOG,
                            message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态) 成功"
                        )
                    }
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_TILE_2X1_LOG,
                        message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                throwable = e
            )
        }
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * 适配 ColorOS V16.0.0
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     */
    private fun hideTileIconBkgOS160(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        try {
            val clazz = loadClass(
                className = "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
                classLoader = param.classLoader
            )
            val method = clazz.getDeclaredMethod(
                "setBackground",
                Drawable::class.java
            )
            module.hook(method).intercept { chain ->
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态) 成功"
                )
                null
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态) 失败",
                throwable = e
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴图标大小
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param iconSizeDp 图标大小 (dp)
     */
    fun modifyTileIconSize(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        iconSizeDp: Float
    ) {
        try {
            val clazz = loadClass(
                "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
                param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 OplusQSIconView 类"
            )
            val spanSizeClass = loadClass(
                "com.oplusos.systemui.common.model.SpanSize",
                param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 SpanSize 类"
            )
            val method = clazz.getDeclaredMethod(
                "getIconSize",
                spanSizeClass,
                Boolean::class.javaPrimitiveType
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 getIconSize 函数"
            )
            module.hook(method).intercept { chain ->
                val view = chain.thisObject as View
                val iconSizePx = iconSizeDp.dpToPx(view.context).roundToInt()
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴图标大小为 ${iconSizeDp}dp"
                )
                iconSizePx
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改图标大小失败: ${e.message}"
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴标签字体颜色
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param inactiveTitleColor 非激活标题颜色
     * @param inactiveDesColor 非激活描述颜色
     * @param activeTitleColor 激活标题颜色
     * @param activeDesColor 激活描述颜色
     */
    fun modifyTileTextColor(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        @ColorInt inactiveTitleColor: Int,
        @ColorInt inactiveDesColor: Int,
        @ColorInt activeTitleColor: Int,
        @ColorInt activeDesColor: Int
    ) {
        try {
            // 相关类获取
            val labelColorManagerClazz = loadClass(
                className = "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSHighlightTileViewLabelColorManager",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 OplusQSHighlightTileViewLabelColorManager 类"
            )
            val stateClazz = loadClass(
                className = "com.android.systemui.plugins.qs.QSTile\$State",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 QSTile\$State 类"
            )
            val pairClazz = loadClass(
                className = "kotlin.Pair",
                classLoader = param.classLoader
            ) ?: return log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 未获取到 Pair 类"
            )
            val method = labelColorManagerClazz.getDeclaredMethod(
                "getColorByTileState",
                Context::class.java,
                stateClazz
            )
            module.hook(method).intercept { chain ->
                val stateObj = chain.args[1]
                val state = stateObj.getAnyField<Int>("state")
                // 依据 state 获取颜色
                val (titleColor, desColor) = when (state) {
                    2 -> activeTitleColor to activeDesColor // Active
                    else -> inactiveTitleColor to inactiveDesColor // Inactive / Unavailable
                }
                // 反射构造 kotlin.Pair(ColorStateList, ColorStateList)
                val resultPair = pairClazz
                    .getConstructor(Any::class.java, Any::class.java)
                    .newInstance(
                        ColorStateList.valueOf(titleColor),
                        ColorStateList.valueOf(desColor)
                    )
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴文本颜色 (state: $state)"
                )
                resultPair
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 修改文本颜色失败: ${e.message}"
            )
        }
    }
}

fun printViewHierarchy(module: XposedModule, view: View, depth: Int = 0) {
    val indent = "  ".repeat(depth)

    // 获取资源 ID 和对应的资源名（如果有）
    val idInfo = if (view.id != View.NO_ID) {
        try {
            val entryName = view.resources.getResourceEntryName(view.id)
            "id=${view.id} (${entryName})"
        } catch (e: Exception) {
            "id=${view.id} (unknown)"
        }
    } else {
        "no_id"
    }

    // 类型（简单类名）
    val type = view.javaClass.simpleName

    // 名称：优先取 contentDescription，否则取类名（也可取 tag）
    val name = view.contentDescription?.toString() ?: type

    // 打印
    log(
        module = module, tag = QS_TILE_2X1_LOG,
        message = "$indent├─ $type | $idInfo | name=$name"
    )

    // 如果是 ViewGroup，递归打印子 View
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            printViewHierarchy(module, view.getChildAt(i), depth + 1)
        }
    }
}