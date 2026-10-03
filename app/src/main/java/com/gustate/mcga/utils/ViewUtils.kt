package com.gustate.mcga.utils

import android.content.Context
import android.content.res.Configuration

object ViewUtils {
    private val Context.density: Float
        get() = resources.displayMetrics.density
    private val Context.fontScale: Float
        get() = resources.configuration.fontScale

    fun Float.dpToPx(context: Context): Float = this * context.density
    fun Float.spToPx(context: Context): Float = this * context.density * context.fontScale
    fun Float.pxToDp(context: Context): Float = this / context.density
    fun Float.pxToSp(context: Context): Float = this / (context.density * context.fontScale)

    /**
     * 判断当前是否为深色模式
     */
    fun Context.isDarkMode(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}