package com.gustate.mcga.xposed.helper

import android.graphics.Path
import kotlin.math.pow

object SquircleHelper {
    fun getSquirclePath(width: Int, height: Int, radius: Float): Path {
        val mPath = Path()
        val left = 0f
        val top = 0f
        val right = width.toFloat()
        val bottom = height.toFloat()
        val smooth = 0.6f
        // 核心数学推导
        val c = 0.2929f * radius
        val b = (1.5f * (2f * c * c).pow(1.5f) / (c * radius))
        val a = radius * (1 + smooth) - c - c - b
        val ab = a + b
        val cb = c + b
        val abc = a + b + c
        val abcc = abc + c
        mPath.reset()
        // 左上角
        mPath.moveTo(left, top + abcc)
        mPath.rCubicTo(0f, -a, 0f, -ab, c, -abc)
        mPath.rCubicTo(c, -c, cb, -c, abc, -c)
        // 右上角
        mPath.lineTo(right - abcc, top)
        mPath.rCubicTo(a, 0f, ab, 0f, abc, c)
        mPath.rCubicTo(c, c, c, cb, c, abc)
        // 右下角
        mPath.lineTo(right, bottom - abcc)
        mPath.rCubicTo(0f, a, 0f, ab, -c, abc)
        mPath.rCubicTo(-c, c, -cb, c, -abc, c)
        // 左下角
        mPath.lineTo(left + abcc, bottom)
        mPath.rCubicTo(-a, 0f, -ab, 0f, -abc, -c)
        mPath.rCubicTo(-c, -c, -c, -cb, -c, -abc)
        mPath.close()
        return mPath
    }
}