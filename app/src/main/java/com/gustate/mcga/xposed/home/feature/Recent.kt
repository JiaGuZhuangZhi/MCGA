package com.gustate.mcga.xposed.home.feature

import android.view.ViewGroup
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object Recent {
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
}