package com.gustate.mcga.xposed

import com.gustate.mcga.xposed.home.HomeHook
import com.gustate.mcga.xposed.search.SearchHook
import com.gustate.mcga.xposed.systemui.SystemuiHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        applyAllFeature(lpparam)
    }

    fun applyAllFeature(lpparam: XC_LoadPackage.LoadPackageParam) {
        HomeHook.applySearchFeature(lpparam)
        SearchHook.applySearchFeature(lpparam)
        SystemuiHook.applySystemUiFeature(lpparam)
    }

}