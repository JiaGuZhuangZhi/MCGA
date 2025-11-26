package com.gustate.mcga.xposed.search

import com.gustate.mcga.data.keys.SearchKeys
import com.gustate.mcga.xposed.search.feature.AppRecommend
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

object SearchHook {
    fun applySearchFeature(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 以下内容仅 Hook 全局搜索 (com.heytap.quicksearchbox)
        if (lpparam.packageName != "com.heytap.quicksearchbox") return
        val prefs = XSharedPreferences(
            "com.gustate.mcga",
            "xposed_prefs"
        )
        prefs.makeWorldReadable()
        val goneAdviceAppName = prefs.getBoolean(
            SearchKeys.HIDE_RECOM_APP_NAME,
            false
        )
        val fixAdviceAppCard = prefs.getBoolean(
            SearchKeys.FIX_RECOM_CARD_HEIGHT,
            false
        )
        if (goneAdviceAppName) AppRecommend.goneAdviceAppName(lpparam)
        if (fixAdviceAppCard) AppRecommend.fixAdviceAppCard(lpparam)
    }
}