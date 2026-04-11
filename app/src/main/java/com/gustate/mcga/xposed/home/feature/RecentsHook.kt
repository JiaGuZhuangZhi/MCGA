package com.gustate.mcga.xposed.home.feature

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gustate.mcga.utils.LogUtils.log
import com.gustate.mcga.xposed.helper.ClassHelper.getAnyField
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class RecentsHook {

    companion object {
        const val TAG = "最近任务"
    }

    /**
     * 修改“清除全部”按钮样式
     */
    fun customizeClearAllButton(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam
    ) {
        // 获取 ClassLoader 实例
        val classLoader = param.classLoader
        try {
            // 加载类
            // 加载清除按钮类
            val panelClass = loadClass(
                className = "com.oplus.quickstep.views.OplusClearAllPanelView",
                classLoader = classLoader
            ) ?: return log(
                module = module, tag = TAG,
                message = "❌ 未获取到 OplusClearAllPanelView 类"
            )
            // hook 布局加载完成
            val onFinishInflate = panelClass
                .getDeclaredMethod("onFinishInflate")
                ?: return log(
                    module = module, tag = TAG,
                    message = "❌ 未获取到 onFinishInflate 函数"
                )
            module.hook(onFinishInflate).intercept { chain ->
                // 执行原逻辑
                val result = chain.proceed()
                // 取 View
                val panelView = chain.thisObject as? ViewGroup
                    ?: throw NullPointerException()
                val clearBtn = panelView.getAnyField<TextView>("mClearAllBtn")
                // 替换按钮文字
                clearBtn?.apply {
                    text = "✕"
                    textSize = 20f
                }
                log(
                    module = module, tag = TAG,
                    message = "✅ 已成功修改最近任务按钮"
                )
                result
            }

            // hook 尺寸更新逻辑 (实现正圆)
            // 这个方法在 ColorOS 里负责计算按钮的宽高
            val updateSizeMethod = panelClass
                .getDeclaredMethod("updateClearAllSize")
                ?: return log(
                    module = module, tag = TAG,
                    message = "❌ 未获取到 updateClearAllSize 函数"
                )
            module.hook(updateSizeMethod).intercept { chain ->
                // 执行原逻辑
                val result = chain.proceed()
                // 取 View
                val panelView = chain.thisObject as? ViewGroup
                    ?: throw NullPointerException()
                val clearBtn = panelView.getAnyField<View>("mClearAllBtn")

                // 将宽度强制设为与高度一致，形成 1:1 正方形
                clearBtn?.layoutParams?.let { lp ->
                    if (clearBtn.measuredHeight > 0) {
                        lp.width = clearBtn.measuredHeight
                        clearBtn.layoutParams = lp
                    }
                }
                result
            }
            log(
                module = module, tag = TAG,
                message = "✅ 已重置清除按钮比例为 1:1"
            )
        } catch (e: Exception) {
            log(
                module = module, tag = TAG,
                message = "❌ 最近任务按钮修改失败 错误信息: ${e.message}"
            )
        }
    }
}