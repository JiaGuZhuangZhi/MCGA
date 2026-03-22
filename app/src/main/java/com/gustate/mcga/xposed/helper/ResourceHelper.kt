package com.gustate.mcga.xposed.helper

import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.gustate.mcga.utils.ViewUtils.dpToPx
import io.github.libxposed.api.XposedModule
import kotlin.math.roundToInt

/**
 * 资源拦截助手类
 * 用于在 libxposed 环境下替换系统资源返回值
 */
object ResourceHelper {

    /**
     * 通过 ClassLoader 反射获取目标包的资源 ID
     * @param classLoader 目标进程的 ClassLoader
     * @param pkgName 目标包名 (如 "com.android.systemui")
     * @param resType 资源类型 (如 "bool", "dimen", "string")
     * @param resName 资源名称 (如 "config_panoramic_all_day")
     * @return 资源 ID，找不到则返回 0
     */
    fun getIdentifier(
        classLoader: ClassLoader,
        pkgName: String,
        resType: String,
        resName: String
    ): Int {
        return runCatching {
            val rClass = classLoader.loadClass("$pkgName.R\$$resType")
            val field = rClass.getDeclaredField(resName)
            field.isAccessible = true
            field.getInt(null)
        }.getOrDefault(0)
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
            )
        module.hook(method).intercept { chain ->
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