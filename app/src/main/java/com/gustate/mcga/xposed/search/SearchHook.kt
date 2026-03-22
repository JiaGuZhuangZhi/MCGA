package com.gustate.mcga.xposed.search

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.SearchKeys
import com.gustate.mcga.xposed.search.feature.AppRecommendHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class SearchHook : XposedModule() {

    private val appRecommendHook = AppRecommendHook()

    /**
     * 成功实例化软件包加载器
     * @param param 正在装载的软件包信息
     */
    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        // 以下内容仅 Hook 全局搜索 (com.heytap.quicksearchbox)
        if (param.packageName != "com.heytap.quicksearchbox") return

        // 获取 Lsposed 远程配置
        val prefs = getRemotePreferences("mcga_prefs")

        // 应用配置
        applyAppRecommendFeature(param = param, prefs = prefs)

    }

    /**
     * 应用应用推荐外观配置
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyAppRecommendFeature(
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val goneAdviceAppName = prefs.getBoolean(
            SearchKeys.HIDE_RECOM_APP_NAME,
            false
        )
        val fixAdviceAppCard = prefs.getBoolean(
            SearchKeys.FIX_RECOM_CARD_HEIGHT,
            false
        )
        if (goneAdviceAppName) appRecommendHook
            .goneAdviceAppName(module = this, param = param)
        if (fixAdviceAppCard) appRecommendHook
            .fixAdviceAppCard(module = this, param = param)
    }

}