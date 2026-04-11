package com.gustate.mcga.xposed

import android.content.SharedPreferences
import com.gustate.mcga.xposed.aod.AodHook
import com.gustate.mcga.xposed.home.HomeHook
import com.gustate.mcga.xposed.search.SearchHook
import com.gustate.mcga.xposed.systemui.SystemuiHook
import com.gustate.mcga.xposed.wallet.WalletHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class MainHook : XposedModule() {

    /**
     * 成功实例化软件包加载器
     * @param param 正在装载的软件包信息
     */
    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        // 获取 Lsposed 远程配置
        val prefs = getRemotePreferences("mcga_prefs")

        // 应用全部 Hook 设置
        applyAllFeature(
            param = param,
            prefs = prefs
        )
    }

    /**
     * 应用全部 Hook 设置
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    private fun applyAllFeature(
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 全局搜索 (com.heytap.quicksearchbox)
        SearchHook.applySearchFeature(
            module = this,
            param = param,
            prefs = prefs
        )
        // 系统桌面 (com.android.launcher)
        HomeHook.applyHomeFeature(
            module = this,
            param = param,
            prefs = prefs
        )
        // 系统界面 (com.android.systemui)
        SystemuiHook.applySystemuiFeature(
            module = this,
            param = param,
            prefs = prefs
        )
        // 息屏 (com.oplus.aod)
        AodHook.applyAodFeature(
            module = this,
            param = param,
            prefs = prefs
        )
        // 钱包 (com.finshell.wallet)
        WalletHook.applyWalletFeature(
            module = this,
            param = param,
            prefs = prefs
        )
    }
}