package com.gustate.mcga.xposed.search

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.SearchKeys
import com.gustate.mcga.xposed.search.feature.AppRecommendHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object SearchHook {

    // 实例化相关 Feature 类
    private val appRecommendHook = AppRecommendHook()

    /**
     * 应用全局搜索 Hook 设置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applySearchFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 以下内容仅 Hook 全局搜索 (com.heytap.quicksearchbox)
        if (param.packageName != "com.heytap.quicksearchbox") return

        // 应用配置
        applyAppRecommendFeature(module = module, param = param, prefs = prefs)

    }

    /**
     * 应用应用推荐外观配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyAppRecommendFeature(
        module: XposedModule,
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
            .goneAdviceAppName(module = module, param = param)
        if (fixAdviceAppCard) appRecommendHook
            .fixAdviceAppCard(module = module, param = param)
    }

}