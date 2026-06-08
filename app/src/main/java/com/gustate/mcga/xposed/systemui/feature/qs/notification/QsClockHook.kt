package com.gustate.mcga.xposed.systemui.feature.qs.notification

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.utils.ViewUtils.dpToPx
import com.gustate.mcga.xposed.helper.ClassHelper.callAnyMethod
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import com.gustate.mcga.xposed.helper.ClassHelper.setAnyField
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import kotlin.math.abs

class QsClockHook {

    companion object {
        const val QS_CLOCK_LOG = "通知中心顶部时钟"
    }

    /**
     * 修改控制中心时钟布局与外观
     * 适配 ColorOS / OxygenOS (OplusSystemUI)
     * @param module 当前 XposedModule 实例
     * @param param 软件包加载参数
     * @param clockScaleMultiplier 时钟缩放倍数 (1.0 为原始大小)
     * @param containerHeightDp 容器高度 (dp)
     */
    fun modifyQSClockLayout(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        clockScaleMultiplier: Float = 1.3f,
        containerHeightDp: Float = 120f
    ) {
        val classLoader = param.classLoader
        try {
            val fakeControllerClazz = loadClass(
                className = "com.oplus.systemui.separate.OplusSimpleQSFakeController",
                classLoader = classLoader
            )
            val simpleHeaderClazz = loadClass(
                className = "com.oplus.systemui.separate.OplusQSSimpleHeader",
                classLoader = classLoader
            )

            modifyClockScale(
                module = module,
                fakeControllerClazz = fakeControllerClazz,
                simpleHeaderClazz = simpleHeaderClazz,
                clockScaleMultiplier = clockScaleMultiplier
            )
            modifyClockConstraints(
                module = module,
                simpleHeaderClazz = simpleHeaderClazz,
                containerHeightDp = containerHeightDp
            )
            hideSettingsButton(
                module = module,
                simpleHeaderClazz = simpleHeaderClazz
            )

        } catch (e: Exception) {
            log(
                module = module, tag = QS_CLOCK_LOG,
                message = "❌ 初始化 QS 时钟布局 Hook 失败",
                throwable = e
            )
        }
    }

// ─────────────────────────────────────────────
// region 时钟缩放
// ─────────────────────────────────────────────

    /**
     * Hook updateQSClockScale，在原始缩放基础上叠加自定义倍数，
     * 并在缩放完成后一次性修正 pivot 至视图中心。
     */
    private fun modifyClockScale(
        module: XposedModule,
        fakeControllerClazz: Class<*>,
        simpleHeaderClazz: Class<*>,
        clockScaleMultiplier: Float
    ) {
        try {
            val updateQSClockScale = fakeControllerClazz
                .getDeclaredMethod("updateQSClockScale", Float::class.javaPrimitiveType)

            module.hook(updateQSClockScale).intercept { chain ->
                try {
                    val controller = chain.thisObject
                    val context = controller.getAnyField<Context>("context")

                    val originalScale = chain.args[0] as Float
                    val fontScale = context.resources.configuration.fontScale
                    val finalScale =
                        ((fontScale + 0.5f) * originalScale + 1.0f) * clockScaleMultiplier

                    val finalArgs = chain.args.toMutableList()
                    finalArgs[0] = finalScale
                    val result = chain.proceed(finalArgs.toTypedArray())

                    // 缩放写入后，一次性修正 pivot，不挂持续监听
                    val header = controller.getAnyField<Any>("oplusQSSimpleHeader")
                    val clockView = header.callAnyMethod<View>("getClockView")
                    Handler(Looper.getMainLooper()).post {
                        if (clockView.width > 0 && clockView.height > 0) {
                            clockView.pivotX = clockView.width / 2f
                            clockView.pivotY = clockView.height / 2f
                        }
                        if (clockView is TextView) {
                            clockView.gravity = Gravity.CENTER
                            clockView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                            clockView.alpha = 0.8f
                        }
                        protectClockViewFromSystem(clockView)
                    }

                    /**log(
                    module = module, tag = QS_CLOCK_LOG,
                    message = "✅ 时钟缩放修改成功：原始=$originalScale 最终=$finalScale"
                    )*/
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_CLOCK_LOG,
                        message = "❌ 修改时钟缩放失败",
                        throwable = e
                    )
                    return@intercept chain.proceed()
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_CLOCK_LOG,
                message = "❌ Hook updateQSClockScale 失败",
                throwable = e
            )
        }
    }

    private fun protectClockViewFromSystem(clockView: View) {
        // 方法2：使用ViewTreeObserver监听绘制，确保支点正确
        clockView.viewTreeObserver.addOnDrawListener {
            // 在绘制时检查支点
            if (clockView.width > 0 && clockView.height > 0) {
                val targetPivotX = clockView.width / 2f
                val targetPivotY = clockView.height / 2f

                // 如果支点不正确，立即修正并请求重绘
                if (abs(clockView.pivotX - targetPivotX) > 0.1f ||
                    abs(clockView.pivotY - targetPivotY) > 0.1f
                ) {
                    clockView.pivotX = targetPivotX
                    clockView.pivotY = targetPivotY
                    clockView.invalidate()
                }
            }
        }
    }

// endregion

// ─────────────────────────────────────────────
// region 布局约束 & 容器高度
// ─────────────────────────────────────────────

    /**
     * Hook OplusQSSimpleHeader.onInit / onConfigurationChanged，
     * 在初始化和横竖屏切换时一次性修正时钟、日期约束与容器高度，
     * 避免全局 Hook LayoutInflater / View.setLayoutParams。
     */
    private fun modifyClockConstraints(
        module: XposedModule,
        simpleHeaderClazz: Class<*>,
        containerHeightDp: Float
    ) {
        try {
            val onInit = simpleHeaderClazz.getDeclaredMethod("onInit")
            val onConfigChanged = simpleHeaderClazz.getDeclaredMethod(
                "onConfigurationChanged",
                Configuration::class.java
            )

            // Hook onInit 去修改高度与约束
            module.hook(onInit).intercept { chain ->
                val result = chain.proceed()
                try {
                    val header = chain.thisObject as View
                    val context = header.context
                    // 容器高度
                    listOf("settingsContainer", "qsButtonContainer").forEach { fieldName ->
                        header.getAnyField<ViewGroup>(fieldName)
                            .setHeightDp(context, containerHeightDp)
                    }
                    // 时钟约束
                    header.getAnyField<View>("clockView")
                        .applyClockConstraint(context)
                    // 日期约束
                    header.getAnyField<View>("dateView")
                        .applyDateConstraint(context)
                    log(
                        module = module, tag = QS_CLOCK_LOG,
                        message = "✅ 时钟布局约束修正完成"
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_CLOCK_LOG,
                        message = "❌ 修正时钟布局约束失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }

            // onConfigurationChanged：延迟等视图更新完毕
            module.hook(onConfigChanged).intercept { chain ->
                val result = chain.proceed()
                try {
                    val header = chain.thisObject as View
                    val context = header.context
                    // 容器高度
                    listOf("settingsContainer", "qsButtonContainer").forEach { fieldName ->
                        header.getAnyField<ViewGroup>(fieldName)
                            .setHeightDp(context, containerHeightDp)
                    }
                    // 时钟约束
                    header.getAnyField<View>("clockView")
                        .applyClockConstraint(context)
                    // 日期约束
                    header.getAnyField<View>("dateView")
                        .applyDateConstraint(context)
                    log(
                        module = module, tag = QS_CLOCK_LOG,
                        message = "✅ 时钟布局约束修正完成"
                    )
                    return@intercept result
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_CLOCK_LOG,
                        message = "❌ 修正时钟布局约束失败",
                        throwable = e
                    )
                    return@intercept result
                }
            }
        } catch (e: Exception) {
            log(
                module = module, tag = QS_CLOCK_LOG,
                message = "❌ Hook 时钟布局约束失败",
                throwable = e
            )
        }
    }

// endregion

// ─────────────────────────────────────────────
// region 隐藏设置按钮
// ─────────────────────────────────────────────

    /**
     * Hook OplusQSSimpleHeader 三处时机，确保 settingsButton 保持隐藏。
     * updateClickAbilities 使用 before 防止方法内部将按钮重新显示。
     */
    private fun hideSettingsButton(
        module: XposedModule,
        simpleHeaderClazz: Class<*>
    ) {
        try {
            val onInit = simpleHeaderClazz
                .getDeclaredMethod("onInit")
            val onConfigChanged = simpleHeaderClazz.getDeclaredMethod(
                "onConfigurationChanged",
                Configuration::class.java
            )
            val updateClickAbilities = simpleHeaderClazz
                .getDeclaredMethod("updateClickAbilities")

            val hideButton: (Any) -> Unit = { header ->
                try {
                    header.getAnyField<ImageView>(
                        fieldName = "settingsButton"
                    ).apply {
                        visibility = View.GONE
                        alpha = 0f
                    }
                } catch (e: Exception) {
                    log(
                        module = module, tag = QS_CLOCK_LOG,
                        message = "❌ 隐藏 settingsButton 失败",
                        throwable = e
                    )
                }
            }

            module.hook(onInit).intercept { chain ->
                val result = chain.proceed()
                hideButton(chain.thisObject)
                return@intercept result
            }

            module.hook(onConfigChanged).intercept { chain ->
                val result = chain.proceed()
                Handler(Looper.getMainLooper()).postDelayed({
                    hideButton(chain.thisObject)
                }, 200)
                return@intercept result
            }

            // before：防止方法执行过程中把按钮重新设为可见
            module.hook(updateClickAbilities).intercept { chain ->
                hideButton(chain.thisObject)
                val result = chain.proceed()
                return@intercept result
            }

            log(
                module = module, tag = QS_CLOCK_LOG,
                message = "✅ settingsButton 隐藏 Hook 注册完成"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = QS_CLOCK_LOG,
                message = "❌ Hook settingsButton 隐藏失败",
                throwable = e
            )
        }
    }

// endregion

// ─────────────────────────────────────────────
// region 私有扩展 / 工具
// ─────────────────────────────────────────────

    private fun View.setHeightDp(context: Context, dp: Float) {
        val params = layoutParams ?: return
        params.height = dp.dpToPx(context).toInt()
        layoutParams = params
    }

    private fun View.applyClockConstraint(context: Context) {
        val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
        if (!params.javaClass.name.contains("ConstraintLayout", ignoreCase = true)) return
        params.setAnyField("topToBottom", -1)
        params.setAnyField("bottomToBottom", -1)
        params.setAnyField("baselineToBaseline", -1)
        params.setAnyField("topToTop", 0)
        params.setAnyField("startToStart", 0)
        params.setAnyField("endToEnd", 0)
        params.setAnyField("topMargin", 36f.dpToPx(context).toInt())
        layoutParams = params
    }

    private fun View.applyDateConstraint(context: Context) {
        val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
        if (!params.javaClass.name.contains("ConstraintLayout", ignoreCase = true)) return
        params.setAnyField("topToTop", -1)
        params.setAnyField("bottomToBottom", -1)
        params.setAnyField("baselineToBaseline", -1)
        params.setAnyField("endToStart", -1)
        val clockId = findSiblingClockId()
        if (clockId != 0) {
            params.setAnyField("topToBottom", clockId)
            params.setAnyField("topMargin", 30f.dpToPx(context).toInt())
        } else {
            params.setAnyField("topToTop", 0)
        }
        params.setAnyField("startToStart", 0)
        params.setAnyField("endToEnd", 0)
        layoutParams = params
        if (this is TextView) gravity = Gravity.CENTER_HORIZONTAL
    }

    private fun View.findSiblingClockId(): Int {
        val parent = parent as? ViewGroup ?: return 0
        for (i in 0 until parent.childCount) {
            val sibling = parent.getChildAt(i)
            if (sibling !== this && sibling.javaClass.name.contains("SimpleQsClock")) {
                return sibling.id
            }
        }
        return 0
    }

}