package com.gustate.mcga.xposed.wallet

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.WalletKeys
import com.gustate.mcga.xposed.wallet.feature.NearmeHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

object WalletHook {

    // 实例化相关 Feature 类
    private val nearmeHook = NearmeHook()

    /**
     * 应用钱包 Hook 设置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyWalletFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        // 以下内容仅 Hook 钱包 (com.finshell.wallet)
        if (param.packageName != "com.finshell.wallet") return

        // 应用配置
        applyNearmeFeature(module = module, param = param, prefs = prefs)

    }

    /**
     * 应用 NFC 消费界面外观配置
     * @param module 当前 XposedModule 实例
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyNearmeFeature(
        module: XposedModule,
        param: XposedModuleInterface.PackageReadyParam,
        prefs: SharedPreferences
    ) {
        val enableCustomNfcCardPage = prefs.getBoolean(
            WalletKeys.ENABLE_CUSTOM_NFC_CARD_PAGE,
            false
        )
        val nfcCardPageBkgBlurRadius = prefs.getInt(
            WalletKeys.NFC_CARD_PAGE_BKG_BLUR_RADIUS,
            255
        )
        val nfcCardPageBkgScrimLight = prefs.getInt(
            WalletKeys.NFC_CARD_PAGE_BKG_SCRIM_LIGHT,
            0X14FFFFFF
        )
        val nfcCardPageBkgScrimDark = prefs.getInt(
            WalletKeys.NFC_CARD_PAGE_BKG_SCRIM_DARK,
            0X66000000
        )
        val nfcCardPageWidgetSquircle = prefs.getBoolean(
            WalletKeys.NFC_CARD_PAGE_WIDGET_SQUIRCLE,
            true
        )
        val nfcCardPageWidgetAlpha = prefs.getFloat(
            WalletKeys.NFC_CARD_PAGE_WIDGET_ALPHA,
            0.42f
        )
        val nfcCardPageWidgetCornerRadius = prefs.getFloat(
            WalletKeys.NFC_CARD_PAGE_WIDGET_CORNER_RADIUS,
            24f
        )

        if (enableCustomNfcCardPage) {
            nearmeHook.changeNfcConsume(
                module = module,
                param = param,
                blurRadius = nfcCardPageBkgBlurRadius,
                blurScrimLight = nfcCardPageBkgScrimLight,
                blurScrimDark = nfcCardPageBkgScrimDark,
                widgetSquircle = nfcCardPageWidgetSquircle,
                widgetAlpha = nfcCardPageWidgetAlpha,
                widgetCornerRadius = nfcCardPageWidgetCornerRadius
            )
        }
    }
}