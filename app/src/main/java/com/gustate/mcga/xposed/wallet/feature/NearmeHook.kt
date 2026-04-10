package com.gustate.mcga.xposed.wallet.feature

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isNotEmpty
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ResourceHelper.getIdentifier
import com.gustate.mcga.xposed.helper.SquircleDrawable
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import kotlin.math.roundToInt

/**
 * Nearme 钱包相关功能拦截
 */
class NearmeHook {

    companion object {
        const val NFC_LOG = "NFC"
    }

    /**
     * 修改 NFC 消费界面外观 (模糊、圆角、透明度)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param blurRadius 窗口模糊半径
     * @param blurScrimLight 浅色模式覆盖色
     * @param blurScrimDark 深色模式覆盖色
     * @param widgetSquircle 是否启用超椭圆 (Squircle) 绘制
     * @param widgetAlpha 组件透明度 (0.0f - 1.0f)
     * @param widgetCornerRadius 组件圆角大小 (dp)
     */
    fun changeNfcConsume(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        blurRadius: Int = 256,
        blurScrimLight: Int = 0X14FFFFFF,
        blurScrimDark: Int = 0X66000000,
        widgetSquircle: Boolean = true,
        widgetAlpha: Float = 0.2f,
        widgetCornerRadius: Float = 28f
    ) {

        val systemStateManagerClass = loadClass(
            "com.nearme.common.taskmgr.SystemStateManager",
            param.classLoader
        ) ?: return
        // 找到 addFloatStateView 方法
        val addFloatStateView = systemStateManagerClass.declaredMethods
            .find { it.name == "addFloatStateView" } ?: return

        module.hook(addFloatStateView).intercept { chain ->
            val result = chain.proceed()
            // 第一个参数就是传入的 ViewGroup (content)
            val viewGroup = chain.args[0] as? ViewGroup ?: return@intercept result
            // 遍历清除所有子 View 背景
            for (i in 0 until viewGroup.childCount) {
                viewGroup.getChildAt(i)?.setBackgroundColor(Color.TRANSPARENT)
            }
            result
        }

        val nfcActivityClass = loadClass(
            className = "com.nearme.wallet.nfc.ui.NfcConsumeActivity",
            classLoader = param.classLoader
        ) ?: return
        // 处理窗口模糊与背景
        try {

            val onCreate = nfcActivityClass
                .getDeclaredMethod("onCreate", Bundle::class.java)
            module.hook(onCreate).intercept { chain ->
                val result = chain.proceed()

                val activity = chain.thisObject as Activity
                val res = activity.resources
                val window = activity.window

                // 判断深色模式
                val isDark = (activity.resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                val widgetCornerPx = widgetCornerRadius.dpToPx(context = activity).roundToInt()
                val scrimColor = if (isDark) blurScrimDark else blurScrimLight

                // 配置窗口渲染属性
                window.setBackgroundDrawable(scrimColor.toDrawable())
                window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER)
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                window.attributes.apply {
                    format = PixelFormat.TRANSLUCENT
                    blurBehindRadius = blurRadius
                    dimAmount = 0f
                }.also { window.attributes = it }
                // 清除背景色
                val content = activity.findViewById<ViewGroup>(android.R.id.content)
                content.viewTreeObserver.addOnGlobalLayoutListener {
                    // 清理所有层级的背景
                    var current: View? = content
                    while (current is FrameLayout) {
                        current.setBackgroundColor(Color.TRANSPARENT)
                        if (current.isNotEmpty()) {
                            current = current.getChildAt(0)
                        } else break
                    }
                    // 为卡片设置背景
                    val cards = listOf("bus_detail", "layout_operate", "root_layout")
                    getIdentifier(
                        res = res,
                        pkgName = param.packageName,
                        resType = "id",
                        resNames = cards,
                        onReadyResIds = { resIds ->
                            // 分类正确与错误的 ids
                            val (valid, invalid) = resIds.entries.partition { it.value != 0 }
                            // 错误报日志
                            invalid.forEach {
                                log(
                                    module = module, tag = NFC_LOG,
                                    message = "❌ 获取 ${it.key} 资源 ID 失败"
                                )
                            }
                            // 正确走流程
                            valid.forEach {
                                activity.findViewById<ViewGroup>(it.value)?.let { parent ->
                                    parent.background = if (widgetSquircle) {
                                        SquircleDrawable(widgetCornerPx).apply {
                                            val baseColor =
                                                if (isDark) Color.BLACK else Color.WHITE
                                            setColors(normal = baseColor, pressed = baseColor)
                                            alpha = (widgetAlpha * 255).toInt()
                                        }
                                    } else {
                                        parent.background
                                            ?.apply { alpha = (widgetAlpha * 255).toInt() }
                                    }
                                }
                            }
                        }
                    )
                }
                result
            }
        } catch (e: Exception) {
            log(
                module = module, tag = NFC_LOG,
                message = "❌ 修改 NFC 消费界面失败: ${e.message}"
            )
        }
        // 隐藏刷卡页面功能列表中的按压背景
        val viewHolderClass = loadClass(
            className = "com.nearme.operate.widget.OperateViewHolder",
            classLoader = param.classLoader
        ) ?: return log(
            module = module, tag = NFC_LOG,
            message = "❌ 获取 OperateViewHolder 类失败"
        )
        try {
            val constructor = viewHolderClass.getDeclaredConstructor(
                View::class.java
            ) ?: return log(
                module = module, tag = NFC_LOG,
                message = "❌ 获取 OperateViewHolder 构造函数失败"
            )
            module.hook(constructor).intercept { chain ->
                val result = chain.proceed()
                val itemView = chain.args[0] as? ViewGroup
                // 干掉子 View 的背景
                itemView?.getChildAt(0)?.background = 0.toDrawable()
                result
            }
        } catch (e: Exception) {
            log(
                module = module, tag = NFC_LOG,
                message = "❌ Hook OperateViewHolder 失败: ${e.message}"
            )
        }
    }
}