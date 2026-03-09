package com.gustate.mcga.xposed.wallet

import com.gustate.mcga.data.keys.WalletKeys
import com.gustate.mcga.xposed.wallet.feature.Nearme
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage

object WalletHook {
    fun applyWalletFeature(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 以下内容仅 Hook 钱包 (com.finshell.wallet)
        if (lpparam.packageName != "com.finshell.wallet") return
        val prefs = XSharedPreferences(
            "com.gustate.mcga",
            "xposed_prefs"
        )
        prefs.makeWorldReadable()

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
            Nearme.changeNfcConsume(
                lpparam = lpparam,
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