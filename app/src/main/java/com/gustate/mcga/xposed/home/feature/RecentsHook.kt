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

        // 加载类
        // 加载清除按钮类
        val panelClass = loadClass(
            "com.oplus.quickstep.views.OplusClearAllPanelView",
            classLoader
        )

        // hook 布局加载完成
        val onFinishInflate = panelClass
            ?.getDeclaredMethod("onFinishInflate")
            ?: throw NullPointerException()
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
        val updateSizeMethod = panelClass.getDeclaredMethod("updateClearAllSize")
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
                    log(
                        module = module, tag = TAG,
                        message = "✅ 已重置清除按钮比例为 1:1"
                    )
                }
            }
            result
        }
    }
}

/*object Recent {
    fun changeClearAllButton(
        lpparam: XC_LoadPackage.LoadPackageParam
    ) {
        val panelClassName = "com.oplus.quickstep.views.OplusClearAllPanelView"
        XposedHelpers.findAndHookMethod(
            panelClassName,
            lpparam.classLoader,
            "onFinishInflate",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val panelView = param.thisObject as ViewGroup
                    // 通过反射或字段名获取 mClearAllBtn
                    // 代码显示该字段为 public，可以直接获取
                    val clearBtn = XposedHelpers
                        .getObjectField(panelView, "mClearAllBtn") as? TextView
                        ?: return

                    // 修改文字为 ✕
                    clearBtn.text = "✕"
                    clearBtn.textSize = 20f
                }
            }
        )
        XposedHelpers.findAndHookMethod(
            panelClassName,
            lpparam.classLoader,
            "updateClearAllSize",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val panelView = param.thisObject as ViewGroup
                    val clearBtn = XposedHelpers.getObjectField(
                        panelView,
                        "mClearAllBtn"
                    ) as? android.view.View ?: return

                    val lp = clearBtn.layoutParams
                    if (clearBtn.measuredHeight > 0) {
                        lp.width = clearBtn.measuredHeight
                        clearBtn.layoutParams = lp
                    }
                }
            }
        )
    }
}*/