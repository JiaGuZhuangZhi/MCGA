package com.gustate.mcga.xposed.systemui.feature.qs.tile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_2X1_LOG
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
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
        ) ?: return
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
        val clazz = loadClass(
            "com.oplus.systemui.qs.base.util.QsColorUtil",
            param.classLoader
        ) ?: return
        try {
            val method = clazz.getDeclaredMethod(
                "isNeedUseSeparateDarkThemeColor",
                Context::class.java,
                Boolean::class.javaPrimitiveType
            )
            module.hook(method).intercept { chain ->
                // 获取调用栈，看看是不是那个代理类在调我们
                val stack = Throwable().stackTrace
                val isFromProxy = stack.any {
                    it.className
                        .contains("QsHighlightTileViewBackgroundProxyImpl")
                }

                if (isFromProxy) {
                    log(
                        module = module, tag = QS_TILE_2X1_LOG,
                        message = "✅ 成功使磁贴状态填满 2*1 磁贴 (已堵桥)"
                    )
                    false
                } else {
                    chain.proceed()
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
        val clazz = loadClass(
            "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
            param.classLoader
        ) ?: return
        try {
            val method = clazz.getDeclaredMethod("setBackground", Drawable::class.java)
            module.hook(method).intercept { chain ->
                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态)"
                )
                null
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_TILE_2X1_LOG,
                message = "❌ 隐藏图标背景失败: ${e.message}"
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
        val clazz = loadClass(
            "com.oplus.systemui.plugins.qs.customize.view.tile.OplusQSIconView",
            param.classLoader
        ) ?: return
        val spanSizeClass = loadClass(
            "com.oplusos.systemui.common.model.SpanSize",
            param.classLoader
        ) ?: return
        try {
            val method = clazz.getDeclaredMethod(
                "getIconSize",
                spanSizeClass,
                Boolean::class.javaPrimitiveType
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
        val clazz = loadClass(
            "com.oplus.systemui.plugins.qs.customize.view.tile." +
                    "OplusQSHighlightTileViewLabelColorManager",
            param.classLoader
        ) ?: return
        val stateClass = loadClass(
            "com.android.systemui.plugins.qs.QSTile\$State",
            param.classLoader
        ) ?: return
        val pairClass = loadClass(
            "kotlin.Pair",
            param.classLoader
        ) ?: return

        try {
            val method = clazz.getDeclaredMethod(
                "getColorByTileState",
                Context::class.java,
                stateClass
            )
            module.hook(method).intercept { chain ->

                val stateObj = chain.args[1]
                log(module = module, Log.ERROR, message = stateObj.toString())
                val state = stateObj.getAnyField<Any>("state") as? Int ?: 1

                val (titleColor, desColor) = when (state) {
                    2 -> activeTitleColor to activeDesColor // Active
                    else -> inactiveTitleColor to inactiveDesColor // Inactive / Unavailable
                }

                // 反射构造 kotlin.Pair(ColorStateList, ColorStateList)
                val resultPair = pairClass
                    .getConstructor(Any::class.java, Any::class.java)
                    .newInstance(
                        ColorStateList.valueOf(titleColor),
                        ColorStateList.valueOf(desColor)
                    )

                log(
                    module = module, tag = QS_TILE_2X1_LOG,
                    message = "✅ 成功修改 2*1 磁贴文本颜色 (State: $state)"
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

/*package com.gustate.mcga.xposed.systemui.feature.qs.tile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.systemui.feature.QSTileHook.Companion.QS_TILE_2X1_LOG
import kotlin.math.roundToInt

/**
 * 控制中心 2*1 磁贴 Hook 类
 * Gustate - GPL-v3.0
 */
class TwoXOneTileHook {

    /**
     * 修改控制中心 2*1 磁贴圆角半径
     * @param lpparam 应用程式基础信息
     * @param cornerRadiusDp 2*1 磁贴圆角半径
     */
    fun modifyCornerRadius(
        lpparam: XC_LoadPackage.LoadPackageParam,
        cornerRadiusDp: Float
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.plugins.qs.customize." +
                        "view.tile.OplusQSResizeableTileView",
                lpparam.classLoader,
                "getRadius",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)

                        // 调起基础信息
                        val obj = param?.thisObject
                        val clazzName = obj?.javaClass?.name ?: "null"

                        // 获取 View Context
                        val context = (obj as? View)
                            ?.context
                            ?: throw NullPointerException(
                                "❌ 修改控制中心 2*1 磁贴圆角发生错误" +
                                        "我们无法获取到 $clazzName 的 Context"
                            )

                        // 转换单位
                        val cornerRadiusPx = cornerRadiusDp.dpToPx(context)
                        // 设置返回值
                        param.result = cornerRadiusPx
                        // 输出成功日志
                        log(
                            message = "✅ 成功修改 2*1 磁贴圆角半径为 " +
                                    "$cornerRadiusDp dp 的圆角矩形" +
                                    " (系统默认平滑圆角)",
                            tag = QS_TILE_2X1_LOG
                        )
                    }
                }
            )
        } catch (e: Exception) {
            // 输出错误日志
            log(
                message = "❌ 修改 2*1 磁贴圆角半径失败 " +
                        "错误信息: ${e.message}",
                tag = QS_TILE_2X1_LOG
            )
        }
    }

    /**
     * 使磁贴状态填满控制中心 2*1 磁贴
     * (禁用 2*1 磁贴的分离模式调用)
     * @param lpparam 应用程式基础信息
     */
    fun modifyTileStateFullBkg(
        lpparam: XC_LoadPackage.LoadPackageParam,
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.qs.base.util.QsColorUtil",
                lpparam.classLoader,
                "isNeedUseSeparateDarkThemeColor",
                Context::class.java,
                Boolean::class.java, // z: Boolean
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)

                        // 获取调用栈
                        val stack = Throwable().stackTrace
                        // 堵桥！！寻找 2*1 磁贴的调用
                        val caller = stack.firstOrNull {
                            it.className.contains(
                                other = "QsHighlightTileViewBackgroundProxyImpl"
                            )
                        }

                        // 不存在调用不进行修改
                        if (caller == null) return

                        // 存在调用修改返回值为 false
                        param?.result = false
                        // 输出成功日志
                        log(
                            message = "✅ 成功使磁贴状态填满 2*1 磁贴 " +
                                    "拦截自 ${caller.className}",
                            tag = QS_TILE_2X1_LOG
                        )
                    }
                }
            )
        } catch (e: Exception) {
            // 输出错误日志
            log(
                message = "❌ 使磁贴状态填满 2*1 磁贴失败 " +
                        "错误信息: ${e.message}",
                tag = QS_TILE_2X1_LOG
            )
        }
    }

    /**
     * 隐藏控制中心 2*1 磁贴图标背景 (状态)
     * @param lpparam 应用程式基础信息
     */
    fun hideTileIconBkg(
        lpparam: XC_LoadPackage.LoadPackageParam,
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSIconView",
                lpparam.classLoader,
                "setBackground",
                Drawable::class.java, // drawable: Drawable
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                        // 源代码: getMaskView().setBackground(drawable)
                        // 直接替换掉吧, Oplus 目前貌似没有其他用 IconView 的磁贴
                        // 三段式我没看, 这个问题的反馈需要重点关注
                        // 输出成功日志
                        log(
                            message = "✅ 隐藏控制中心 2*1 磁贴图标背景 (状态)",
                            tag = QS_TILE_2X1_LOG
                        )
                        return null
                    }
                }
            )
        } catch (e: Exception) {
            // 输出错误日志
            log(
                message = "❌ 隐藏控制中心 2*1 磁贴图标背景 (状态)失败 " +
                        "错误信息: ${e.message}",
                tag = QS_TILE_2X1_LOG
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴图标大小
     * @param lpparam 应用程式基础信息
     * @param iconSizeDp 控制中心 2*1 磁贴图标大小
     */
    fun modifyTileIconSize(
        lpparam: XC_LoadPackage.LoadPackageParam,
        iconSizeDp: Float
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSIconView",
                lpparam.classLoader,
                "getIconSize",
                "com.oplusos.systemui.common.model.SpanSize",
                Boolean::class.java, // drawable: Drawable
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)

                        // 调起基础信息
                        val obj = param?.thisObject
                        val clazzName = obj?.javaClass?.name ?: "null"

                        // 获取 View Context
                        val context = (obj as? View)
                            ?.context
                            ?: throw NullPointerException(
                                "❌ 修改控制中心 2*1 磁贴图标大小发生错误" +
                                        "我们无法获取到 $clazzName 的 Context"
                            )

                        // 转换单位
                        val iconSizePx = iconSizeDp.dpToPx(context).roundToInt()
                        // 设置返回值
                        param.result = iconSizePx
                        // 输出成功日志
                        log(
                            message = "✅ 成功修改控制中心 2*1 磁贴图标大小为 " +
                                    "$iconSizeDp dp 大小的图标",
                            tag = QS_TILE_2X1_LOG
                        )
                    }
                }
            )
        } catch (e: Exception) {
            // 输出错误日志
            log(
                message = "❌ 修改控制中心 2*1 磁贴图标大小失败 " +
                        "错误信息: ${e.message}",
                tag = QS_TILE_2X1_LOG
            )
        }
    }

    /**
     * 修改控制中心 2*1 磁贴标签字体颜色
     * @param lpparam 应用程式基础信息
     * @param inactiveTitleColor 控制中心 2*1 磁贴非激活/不可用状态下标题颜色
     * @param inactiveDesColor 控制中心 2*1 磁贴非激活/不可用状态标签颜色
     * @param activeTitleColor 控制中心 2*1 磁贴激活状态下标题颜色
     * @param activeDesColor 控制中心 2*1 磁贴激活状态标签颜色
     */
    fun modifyTileTextColor(
        lpparam: XC_LoadPackage.LoadPackageParam,
        @ColorInt inactiveTitleColor: Int,
        @ColorInt inactiveDesColor: Int,
        @ColorInt activeTitleColor: Int,
        @ColorInt activeDesColor: Int
    ) {
        try {
            XposedHelpers.findAndHookMethod(
                "com.oplus.systemui.plugins.qs.customize.view.tile." +
                        "OplusQSHighlightTileViewLabelColorManager",
                lpparam.classLoader,
                "getColorByTileState",
                Context::class.java,
                "com.android.systemui.plugins.qs.QSTile\$State",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)

                        // 当前 object
                        val stateObj = param?.args[1] ?: return
                        // 获取 state 的具体数值 (0: unavailable, 1: inactive, 2: active)
                        val state = XposedHelpers.getIntField(stateObj, "state")
                        // 获取 SystemUI 的 kotlin.Pair 类
                        val pairClass = XposedHelpers.findClass(
                            "kotlin.Pair",
                            param.thisObject.javaClass.classLoader
                        )

                        // 配置颜色并应用
                        when (state) {

                            // 写这么多样板代码不是懒, 这玩意真的很容易变动
                            // inactive 状态
                            1 -> {
                                // 把普通夜色转换为状态颜色列表 (Oplus 你这么做的意义素少写点重载吗?)
                                val titleColorState = ColorStateList.valueOf(inactiveTitleColor)
                                val desColorState = ColorStateList.valueOf(inactiveDesColor)
                                // 通过反射调用 Pair 的构造函数: public Pair(A first, B second)
                                val resultPair = XposedHelpers.newInstance(
                                    pairClass,
                                    titleColorState,
                                    desColorState
                                )
                                // 设置返回值
                                param.result = resultPair
                            }

                            2 -> {
                                // 把普通夜色转换为状态颜色列表 (Oplus 你这么做的意义素少写点重载吗?)
                                val titleColorState = ColorStateList.valueOf(activeTitleColor)
                                val desColorState = ColorStateList.valueOf(activeDesColor)
                                // 通过反射调用 Pair 的构造函数: public Pair(A first, B second)
                                val resultPair = XposedHelpers.newInstance(
                                    pairClass,
                                    titleColorState,
                                    desColorState
                                )
                                // 设置返回值
                                param.result = resultPair
                            }

                            else -> {
                                // 把普通夜色转换为状态颜色列表 (Oplus 你这么做的意义素少写点重载吗?)
                                val titleColorState = ColorStateList.valueOf(inactiveTitleColor)
                                val desColorState = ColorStateList.valueOf(inactiveDesColor)
                                // 通过反射调用 Pair 的构造函数: public Pair(A first, B second)
                                val resultPair = XposedHelpers.newInstance(
                                    pairClass,
                                    titleColorState,
                                    desColorState
                                )
                                // 设置返回值
                                param.result = resultPair
                            }
                        }

                        // 输出成功日志
                        log(
                            message = "✅ 成功修改控制中心 2*1 磁贴标签字体颜色为 " +
                                    "激活状态标题: $activeTitleColor " +
                                    "激活状态标签: $activeDesColor",
                            tag = QS_TILE_2X1_LOG
                        )
                    }
                }
            )
        } catch (e: Exception) {
            // 输出错误日志
            log(
                message = "❌ 修改控制中心 2*1 磁贴标签字体颜色失败 " +
                        "错误信息: ${e.message}",
                tag = QS_TILE_2X1_LOG
            )
        }
    }
}*/