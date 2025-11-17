package com.gustate.mcga.xposed.helper

import android.content.res.Resources
import android.graphics.drawable.Drawable
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object ResourceHelper {

    /**
     * Hook getString(int) → 返回自定义字符串
     */
    fun hookString(resId: Int, newValue: String, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getString",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newValue
                    }
                }
            }
        )
    }

    /**
     * Hook getString(int, Object...) → 支持格式化字符串
     */
    fun hookStringFormatted(resId: Int, newValue: String, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getString",
            Int::class.javaPrimitiveType,
            Array<Any>::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newValue
                    }
                }
            }
        )
    }

    /**
     * Hook getColor(int) → 返回自定义颜色（ARGB 整数）
     */
    fun hookColor(resId: Int, newColor: Int, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getColor",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newColor
                    }
                }
            }
        )
    }

    /**
     * Hook getDimensionPixelSize(int) → 返回自定义尺寸（px）
     */
    fun hookDimensionPixelSize(resId: Int, newSizePx: Int, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getDimensionPixelSize",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newSizePx
                    }
                }
            }
        )
    }

    /**
     * Hook getBoolean(int) → 返回自定义布尔值
     */
    fun hookBoolean(resId: Int, newValue: Boolean, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getBoolean",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newValue
                    }
                }
            }
        )
    }

    /**
     * Hook getInteger(int) → 返回自定义整数值
     */
    fun hookInteger(resId: Int, newValue: Int, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getInteger",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newValue
                    }
                }
            }
        )
    }

    /**
     * Hook getDrawable(int) → 返回自定义 Drawable（注意：需在目标进程中构造）
     * ⚠️ 此方法较复杂，通常建议用资源 ID 替换或仅修改颜色/尺寸
     */
    fun hookDrawable(resId: Int, newDrawable: Drawable, classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod(
            Resources::class.java,
            "getDrawable",
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (param.args[0] == resId) {
                        param.result = newDrawable
                    }
                }
            }
        )
    }

    /**
     * 批量 Hook 字符串资源（可选扩展）
     */
    fun hookStrings(mapping: Map<Int, String>, classLoader: ClassLoader) {
        mapping.forEach { (id, value) -> hookString(id, value, classLoader) }
    }

}