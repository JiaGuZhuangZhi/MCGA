package com.gustate.mcga.xposed.wallet.feature

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isNotEmpty
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.SquircleDrawable
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.math.roundToInt

object Nearme {
    fun changeNfcConsume(
        lpparam: XC_LoadPackage.LoadPackageParam,
        blurRadius: Int = 256,
        blurScrimLight: Int = 0X14FFFFFF,
        blurScrimDark: Int = 0X66000000,
        widgetSquircle: Boolean = true,
        widgetAlpha: Float = 0.2f,
        widgetCornerRadius: Float = 28f
    ) {
        XposedHelpers.findAndHookMethod(
            "com.nearme.wallet.nfc.ui.NfcConsumeActivity",
            lpparam.classLoader,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {

                @SuppressLint("DiscouragedApi")
                override fun afterHookedMethod(param: MethodHookParam) {
                    val activity = param.thisObject as Activity
                    val window = activity.window
                    val res = activity.resources
                    val packageName = activity.packageName

                    // 深色模式
                    val isDark = (activity.resources.configuration.uiMode and
                            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                    // 组件圆角
                    val widgetCornerRadiusPx = widgetCornerRadius.dpToPx(activity).roundToInt()
                    // 模糊覆盖色
                    val scrimColor = if (isDark) blurScrimDark else blurScrimLight

                    // 窗口标签
                    window.setBackgroundDrawable(scrimColor.toDrawable())
                    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
                    window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    window.attributes.apply {
                        format = PixelFormat.TRANSLUCENT // 透明窗口
                        blurBehindRadius = blurRadius // 模糊半径
                        dimAmount = 0f // 去掉黑色遮罩
                    }.also { window.attributes = it }

                    // 延迟处理根布局，因为 SystemStateManager 可能会动态插入 View
                    window.decorView.postDelayed({
                        val content = activity.findViewById<ViewGroup>(android.R.id.content)
                        // 循环清理所有层级的背景
                        var current: View? = content
                        while (current != null) {
                            if (current.id == 2131428022) {
                                current.background = current.background
                                    .apply { alpha = (widgetAlpha * 255).toInt() }
                                break
                            }
                            current.setBackgroundColor(Color.TRANSPARENT)
                            if (current is ViewGroup && current.isNotEmpty()) {
                                current = current.getChildAt(0)
                            } else break
                        }

                        // 获取 layout_operate 的资源 ID
                        val layoutOperateId = res.getIdentifier("layout_operate", "id", packageName)
                        if (layoutOperateId != 0) {
                            val layoutOperate = activity.findViewById<ViewGroup>(layoutOperateId)
                            layoutOperate?.let { parent ->
                                parent.background =
                                    if (widgetSquircle) {
                                        SquircleDrawable(widgetCornerRadiusPx).apply {
                                            val baseColor = if (isDark) Color.BLACK else Color.WHITE
                                            setColors(baseColor, baseColor)
                                            alpha = (widgetAlpha * 255).toInt()
                                        }
                                    } else {
                                        parent.background.apply {
                                            alpha = (widgetAlpha * 255).toInt()
                                        }
                                    }
                            }
                        }

                        // 未激活提示
                        val itemClId = res.getIdentifier("root_layout", "id", packageName)
                        if (itemClId != 0) {
                            val itemCl = activity.findViewById<ViewGroup>(itemClId)
                            log("NFC", itemClId.toString())
                            itemCl?.let { parent ->
                                parent.background.mutate().alpha = (widgetAlpha * 255).toInt()
                            }
                        }

                    }, 300)
                }
            }
        )

        // Hook OperateViewHolder 的构造函数
        XposedHelpers.findAndHookConstructor(
            "com.nearme.operate.widget.OperateViewHolder",
            lpparam.classLoader,
            View::class.java, // 构造函数的参数是 android.view.View
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    // 传入构造函数的 itemView
                    val itemView = param.args[0] as? ViewGroup
                    itemView?.getChildAt(0)?.background = 0.toDrawable()
                }
            }
        )
    }
}