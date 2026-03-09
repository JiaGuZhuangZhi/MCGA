package com.gustate.mcga.xposed.helper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.graphics.toColorInt
import kotlin.math.pow

/**
 * 针对 View 系统优化的 G2 平滑圆角 Drawable
 * 适用于 Xposed 注入 Wallet 等应用，实现 iOS/HyperOS 风格的超椭圆效果
 */
class SquircleDrawable(private val cornerRadiusDp: Int) : Drawable() {

    // 配置：哪些角需要保持直角（用于卡片拼接）
    var skipTopLeft = false
    var skipTopRight = false
    var skipBottomLeft = false
    var skipBottomRight = false

    private val path = Path()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // 状态颜色管理
    private var normalColor = Color.WHITE
    private var pressedColor = "#0F000000".toColorInt()
    private var currentColor = normalColor

    fun setColors(normal: Int, pressed: Int) {
        this.normalColor = normal
        this.pressedColor = pressed
        this.currentColor = normal
        invalidateSelf()
    }

    override fun isStateful(): Boolean = true

    override fun onStateChange(state: IntArray): Boolean {
        val isPressed = state.contains(android.R.attr.state_pressed)
        val newColor = if (isPressed) pressedColor else normalColor
        return if (currentColor != newColor) {
            currentColor = newColor
            invalidateSelf()
            true
        } else false
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updatePath(bounds)
    }

    private fun updatePath(bounds: Rect) {
        val left = bounds.left.toFloat()
        val top = bounds.top.toFloat()
        val right = bounds.right.toFloat()
        val bottom = bounds.bottom.toFloat()

        val radius = cornerRadiusDp.toFloat()
        val smooth = 0.6f // G2 平滑系数

        // 核心数学推导
        val c = 0.2929f * radius
        val b = (1.5f * (2f * c * c).pow(1.5f) / (c * radius))
        val a = radius * (1 + smooth) - c - c - b

        val ab = a + b
        val cb = c + b
        val abc = a + b + c
        val abcc = abc + c

        path.reset()

        // 1. 起点：左上角
        if (skipTopLeft) {
            path.moveTo(left, top)
        } else {
            path.moveTo(left, top + abcc)
            path.rCubicTo(0f, -a, 0f, -ab, c, -abc)
            path.rCubicTo(c, -c, cb, -c, abc, -c)
        }

        // 2. 右上角
        path.lineTo(if (skipTopRight) right else right - abcc, top)
        if (!skipTopRight) {
            path.rCubicTo(a, 0f, ab, 0f, abc, c)
            path.rCubicTo(c, c, c, cb, c, abc)
        } else {
            path.lineTo(right, top)
        }

        // 3. 右下角
        path.lineTo(right, if (skipBottomRight) bottom else bottom - abcc)
        if (!skipBottomRight) {
            path.rCubicTo(0f, a, 0f, ab, -c, abc)
            path.rCubicTo(-c, c, -cb, c, -abc, c)
        } else {
            path.lineTo(right, bottom)
        }

        // 4. 左下角
        path.lineTo(if (skipBottomLeft) left else left + abcc, bottom)
        if (!skipBottomLeft) {
            path.rCubicTo(-a, 0f, -ab, 0f, -abc, -c)
            path.rCubicTo(-c, -c, -c, -cb, -c, -abc)
        } else {
            path.lineTo(left, bottom)
        }

        path.close()
    }

    override fun draw(canvas: Canvas) {
        // 获取当前颜色的原始 alpha
        val baseAlpha = Color.alpha(currentColor)
        // 获取通过 setAlpha 设置的 alpha (0-255)
        val setAlpha = paint.alpha
        // 合成最终透明度 (如果是 255 则保持原样)
        val finalAlpha = (baseAlpha * (setAlpha / 255f)).toInt()
        paint.color = currentColor
        // 必须在 setColor 之后设置，否则会被覆盖
        paint.alpha = finalAlpha
        canvas.drawPath(path, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun getAlpha(): Int = paint.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}