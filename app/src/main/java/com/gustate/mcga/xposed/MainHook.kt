package com.gustate.mcga.xposed

import com.gustate.mcga.xposed.aod.AodHook
import com.gustate.mcga.xposed.home.HomeHook
import com.gustate.mcga.xposed.search.SearchHook
import com.gustate.mcga.xposed.systemui.SystemuiHook
import com.gustate.mcga.xposed.wallet.WalletHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook() : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        applyAllFeature(lpparam)
    }

    fun applyAllFeature(lpparam: XC_LoadPackage.LoadPackageParam) {
        HomeHook.applySearchFeature(lpparam)
        SearchHook.applySearchFeature(lpparam)
        SystemuiHook.applySystemUiFeature(lpparam)
        AodHook.applyAodFeature(lpparam)
        WalletHook.applyWalletFeature(lpparam)
        /*XposedHelpers.findAndHookConstructor(
            "com.tencent.wetype.plugin.hld.view.ImeRootView",
            lpparam.classLoader,
            Context::class.java,
            AttributeSet::class.java,
            Int::class.javaPrimitiveType, // 对应 Java 的 int.class
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val imeRootView = param.thisObject as View

                    // 方案 A: 整体透明度 (包含按键文字)
                    // 0.0f (全透) -> 1.0f (不透)
                    imeRootView.alpha = 0.0f


                    // 方案 B: 仅背景透明 (保持按键清晰)
                    // imeRootView.background?.alpha = 200 // 0-255

                    // 关键点：防止被系统或后续逻辑覆盖
                    imeRootView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }
            }
        )*/
    }

}