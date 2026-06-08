package com.gustate.mcga.data.setting.wallet

import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.BooleanSetting
import com.gustate.mcga.data.setting.base.ColorSetting
import com.gustate.mcga.data.setting.base.FloatSetting
import com.gustate.mcga.data.setting.base.IntSetting
import com.gustate.mcga.data.setting.base.SettingPage

/**
 * 钱包页面声明
 */
object WalletPage : SettingPage(
    command = "su -c pkill -f com.finshell.wallet",
    icon = R.drawable.ic_wallet,
    title = R.string.wallet,
    summary = null,
    dependency = null,
    children = listOf(
        EnableCustomNfcCardPage,
        NfcCardPageBkgBlurRadius,
        NfcCardPageBkgScrimLight,
        NfcCardPageBkgScrimDark,
        NfcCardPageWidgetSquircle,
        NfcCardPageWidgetAlpha,
        NfcCardPageWidgetCornerRadius
    )
)

/**
 * 启用自定义 NFC 卡片页
 */
object EnableCustomNfcCardPage : BooleanSetting(
    key = "enable_custom_nfc_card_page",
    default = false,
    icon = R.drawable.architecture,
    title = R.string.enable_custom_settings,
    summary = null,
    dependency = null
)

/**
 * NFC 卡片页背景模糊半径
 */
object NfcCardPageBkgBlurRadius : IntSetting(
    key = "nfc_card_page_bkg_blur_radius",
    default = 255,
    min = 0,
    max = 512,
    icon = R.drawable.blur_on,
    title = R.string.bkg_blur_radius,
    summary = null,
    dependency = EnableCustomNfcCardPage
)

/**
 * NFC 卡片页浅色模式背景遮罩颜色
 */
object NfcCardPageBkgScrimLight : ColorSetting(
    key = "nfc_card_page_bkg_scrim_light",
    default = 0x14FFFFFF,
    icon = R.drawable.format_color_fill,
    title = R.string.bkg_cover_color,
    summary = R.string.light,
    dependency = EnableCustomNfcCardPage
)

/**
 * NFC 卡片页深色模式背景遮罩颜色
 */
object NfcCardPageBkgScrimDark : ColorSetting(
    key = "nfc_card_page_bkg_scrim_dark",
    default = 0x66000000,
    icon = R.drawable.format_color_fill,
    title = R.string.bkg_cover_color,
    summary = R.string.dark,
    dependency = EnableCustomNfcCardPage
)

/**
 * 启用控件平滑圆角
 */
object NfcCardPageWidgetSquircle : BooleanSetting(
    key = "nfc_card_page_widget_squircle",
    default = true,
    icon = R.drawable.difference,
    title = R.string.enable_option_smooth_rounded_corners,
    summary = null,
    dependency = EnableCustomNfcCardPage
)

/**
 * NFC 卡片页控件透明度
 */
object NfcCardPageWidgetAlpha : FloatSetting(
    key = "nfc_card_page_widget_alpha",
    default = 0.42f,
    min = 0f,
    max = 1f,
    icon = R.drawable.opacity,
    title = R.string.option_alpha,
    summary = null,
    dependency = EnableCustomNfcCardPage
)

/**
 * NFC 卡片页控件圆角半径
 */
object NfcCardPageWidgetCornerRadius : FloatSetting(
    key = "nfc_card_page_widget_corner_radius",
    default = 24f,
    min = 0f,
    max = 99f,
    icon = R.drawable.rounded_corner,
    title = R.string.option_corner_radius,
    summary = null,
    dependency = EnableCustomNfcCardPage
)