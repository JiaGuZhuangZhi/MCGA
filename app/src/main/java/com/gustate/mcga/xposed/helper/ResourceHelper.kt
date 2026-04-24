package com.gustate.mcga.xposed.helper

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import io.github.libxposed.api.XposedModule
import kotlin.math.roundToInt

/**
 * 资源拦截助手类
 * 用于在 libxposed 环境下替换系统资源返回值
 */
object ResourceHelper {

    /**
     * 目标资源包获取目标包的资源 ID
     * @param resources 目标资源包
     * @param pkgName 目标包名 (如 "com.android.systemui")
     * @param resType 资源类型 (如 "bool", "dimen", "string")
     * @param resName 资源名称 (如 "config_panoramic_all_day")
     * @param onReadyResId 获取成功时包含资源 ID 的回调
     */
    @SuppressLint("DiscouragedApi")
    fun getIdentifier(
        res: Resources,
        pkgName: String,
        resType: String,
        resName: String,
        onReadyResId: (resId: Int) -> Unit
    ) {
        val resId = res.getIdentifier(resName, resType, pkgName)
        onReadyResId(resId)
    }

    /**
     * 目标资源包获取目标包的资源 ID 列表
     * @param resources 目标资源包
     * @param pkgName 目标包名 (如 "com.android.systemui")
     * @param resType 资源类型 (如 "bool", "dimen", "string")
     * @param resNames 资源名称列表 (如 "config_panoramic_all_day")
     * @param onReadyResIds 获取成功时包含资源 ID 的回调
     */
    @SuppressLint("DiscouragedApi")
    fun getIdentifier(
        res: Resources,
        pkgName: String,
        resType: String,
        resNames: List<String>,
        onReadyResIds: (resIds: Map<String, Int>) -> Unit
    ) {
        resNames.associateWith { name ->
            res.getIdentifier(name, resType, pkgName)
        }.let(block = onReadyResIds)
    }

    /**
     * 通过资源名称获取目标包的资源 ID
     * @param module 当前 XposedModule 实例
     * @param classLoader 目标进程的 ClassLoader
     * @param pkgName 目标包名 (如 "com.android.systemui")
     * @param resType 资源类型 (如 "bool", "dimen", "string")
     * @param resName 资源名称 (如 "config_panoramic_all_day")
     * @param onReadyResId 获取成功时包含资源 ID 的回调
     */
    @SuppressLint("DiscouragedApi")
    fun getIdentifier(
        module: XposedModule,
        classLoader: ClassLoader,
        pkgName: String,
        resType: String,
        resName: String,
        onReadyResId: (resId: Int) -> Unit
    ) {
        val application = loadClass(
            className = "android.app.Application",
            classLoader = classLoader
        )
        val onCreateMethod = application
            ?.getDeclaredMethod("onCreate")
            ?: throw NullPointerException()
        module.hook(onCreateMethod).intercept { chain ->
            chain.proceed()
            val context = ContextHelper.getContext(classLoader)
            val resId = context.resources
                .getIdentifier(resName, resType, pkgName)
            if (resId == 0)
                throw NullPointerException("❌ 未在 $pkgName 中找到 R.${resType}.${resName}")
            onReadyResId(resId)
        }
    }

    /**
     * 通过资源名称列表获取目标包的资源 ID 列表
     * @param module 当前 XposedModule 实例
     * @param classLoader 目标进程的 ClassLoader
     * @param pkgName 目标包名 (如 "com.android.systemui")
     * @param resType 资源类型 (如 "bool", "dimen", "string")
     * @param resNames 资源名称列表 (如 "config_panoramic_all_day")
     * @param onReadyResIds 获取成功时包含资源 ID 的回调
     */
    @SuppressLint("DiscouragedApi")
    fun getIdentifier(
        module: XposedModule,
        classLoader: ClassLoader,
        pkgName: String,
        resType: String,
        resNames: List<String>,
        onReadyResIds: (resIds: Map<String, Int>) -> Unit
    ) {
        val application = loadClass(
            className = "android.app.Application",
            classLoader = classLoader
        )
        val onCreateMethod = application
            ?.getDeclaredMethod("onCreate")
            ?: throw NullPointerException()
        module.hook(onCreateMethod)
            .intercept { chain ->
                chain.proceed()
                val context = ContextHelper.getContext(classLoader)
                resNames.associateWith { name ->
                    context.resources
                        .getIdentifier(name, resType, pkgName)
                }.let(block = onReadyResIds)
            }
    }

    /**
     * Hook getString(int) -> 返回自定义字符串
     * @param module 当前 XposedModule 实例
     * @param resId 目标资源 ID
     * @param newValue 替换后的字符串值
     */
    fun hookString(module: XposedModule, resId: Int, newValue: String) {
        val method = Resources::class.java
            .getDeclaredMethod(
                "getString",
                Int::class.javaPrimitiveType
            )
        module.hook(method).intercept { chain ->
            if (chain.args[0] == resId) newValue else chain.proceed()
        }
    }

    /**
     * Hook getString(int, Object...) -> 支持格式化字符串的替换
     * @param module 当前 XposedModule 实例
     * @param resId 目标资源 ID
     * @param newValue 替换后的字符串值
     */
    fun hookStringFormatted(module: XposedModule, resId: Int, newValue: String) {
        val method = Resources::class.java
            .getDeclaredMethod(
                "getString",
                Int::class.javaPrimitiveType,
                Array<Any>::class.java
            )
        module.hook(method).intercept { chain ->
            if (chain.args[0] == resId) newValue else chain.proceed()
        }
    }

    /**
     * Hook getColor(int) -> 返回自定义颜色
     * @param module 当前 XposedModule 实例
     * @param resId 目标资源 ID
     * @param newColor 替换后的颜色值 (ARGB 整数)
     */
    fun hookColor(module: XposedModule, resId: Int, newColor: Int) {
        val method = Resources::class.java.getDeclaredMethod(
            "getColor",
            Int::class.javaPrimitiveType
        )
        module.hook(method).intercept { chain ->
            if (chain.args[0] == resId) newColor else chain.proceed()
        }
    }

    /**
     * Hook getDimensionPixelSize(int) -> 返回自定义尺寸
     * @param module 当前 XposedModule 实例
     * @param classLoader 目标进程的 ClassLoader
     * @param resId 目标资源 ID
     * @param newSizeDp 替换后的尺寸值 (单位: dp)
     */
    fun hookDimensionPixelSize(
        module: XposedModule,
        classLoader: ClassLoader,
        resId: Int,
        newSizeDp: Float
    ) {
        val method = Resources::class.java
            .getDeclaredMethod(
                "getDimensionPixelSize",
                Int::class.javaPrimitiveType
            ) ?: throw NullPointerException()
        module.hook(method)
            .intercept { chain ->
                if (chain.args[0] == resId) {
                    val context = ContextHelper.getContext(classLoader)
                    newSizeDp.dpToPx(context).roundToInt()
                } else {
                    chain.proceed()
                }
            }
    }

    /**
     * Hook getBoolean(int) -> 返回自定义布尔值
     * @param module 当前 XposedModule 实例
     * @param resId 目标资源 ID
     * @param newValue 替换后的布尔值
     */
    fun hookBoolean(module: XposedModule, resId: Int, newValue: Boolean) {
        val method = Resources::class.java
            .getDeclaredMethod(
                "getBoolean",
                Int::class.javaPrimitiveType
            )
        module.hook(method).intercept { chain ->
            if (chain.args[0] == resId) newValue else chain.proceed()
        }
    }

    /**
     * Hook getInteger(int) -> 返回自定义整数值
     * @param module 当前 XposedModule 实例
     * @param resId 目标资源 ID
     * @param newValue 替换后的整数值
     */
    fun hookInteger(module: XposedModule, resId: Int, newValue: Int) {
        val method = Resources::class.java
            .getDeclaredMethod(
                "getInteger",
                Int::class.javaPrimitiveType
            )
        module.hook(method).intercept { chain ->
            if (chain.args[0] == resId) newValue else chain.proceed()
        }
    }

    /**
     * Hook getDrawable(int) -> 返回自定义 Drawable 实例
     * @param module 当前 XposedModule 实例
     * @param resId 目标资源 ID
     * @param newDrawable 替换后的 Drawable 对象
     */
    fun hookDrawable(module: XposedModule, resId: Int, newDrawable: Drawable) {
        val method = Resources::class.java
            .getDeclaredMethod(
                "getDrawable",
                Int::class.javaPrimitiveType
            )
        module.hook(method).intercept { chain ->
            if (chain.args[0] == resId) newDrawable else chain.proceed()
        }
    }

    /**
     * 批量 Hook 字符串资源
     * @param module 当前 XposedModule 实例
     * @param mapping 资源 ID 与新字符串值的映射 Map
     */
    fun hookStrings(module: XposedModule, mapping: Map<Int, String>) {
        mapping.forEach { (id, value) -> hookString(module, id, value) }
    }
}