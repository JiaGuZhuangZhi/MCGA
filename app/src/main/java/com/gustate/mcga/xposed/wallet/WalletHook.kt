package com.gustate.mcga.xposed.wallet

import android.content.SharedPreferences
import com.gustate.mcga.data.keys.WalletKeys
import com.gustate.mcga.xposed.wallet.feature.NearmeHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class WalletHook : XposedModule() {

    private val nearmeHook = NearmeHook()

    /**
     * 成功实例化软件包加载器
     * @param param 正在装载的软件包信息
     */
    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        // 以下内容仅 Hook 钱包 (com.finshell.wallet)
        if (param.packageName != "com.finshell.wallet") return

        // 获取 Lsposed 远程配置
        val prefs = getRemotePreferences("mcga_prefs")

        // 应用配置
        applyNearmeFeature(param = param, prefs = prefs)

    }

    /**
     * 应用 NFC 消费界面外观配置
     * @param param 正在装载的软件包信息
     * @param prefs 本地配置缓存
     */
    fun applyNearmeFeature(
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
                module = this,
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