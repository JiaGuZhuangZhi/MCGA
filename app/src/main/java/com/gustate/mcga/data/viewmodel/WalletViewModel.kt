package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.WalletKeys
import com.gustate.mcga.data.state.WalletUiState

class WalletViewModel(context: Application) : AndroidViewModel(context) {
    private val repo = XposedRepo.getInstance(context = context)

    private val _uiState = mutableStateOf(
        value = WalletUiState(
            enableCustomNfcCardPage = repo.getBoolean(
                key = WalletKeys.ENABLE_CUSTOM_NFC_CARD_PAGE,
                def = false
            ),
            nfcCardPageBkgBlurRadius = repo.getInt(
                key = WalletKeys.NFC_CARD_PAGE_BKG_BLUR_RADIUS,
                def = 255
            ),
            nfcCardPageBkgScrimLight = repo.getInt(
                key = WalletKeys.NFC_CARD_PAGE_BKG_SCRIM_LIGHT,
                def = 0X14FFFFFF
            ),
            nfcCardPageBkgScrimDark = repo.getInt(
                key = WalletKeys.NFC_CARD_PAGE_BKG_SCRIM_DARK,
                def = 0X66000000
            ),
            nfcCardPageWidgetSquircle = repo.getBoolean(
                key = WalletKeys.NFC_CARD_PAGE_WIDGET_SQUIRCLE,
                def = true
            ),
            nfcCardPageWidgetAlpha = repo.getFloat(
                key = WalletKeys.NFC_CARD_PAGE_WIDGET_ALPHA,
                def = 0.42f
            ),
            nfcCardPageWidgetCornerRadius = repo.getFloat(
                key = WalletKeys.NFC_CARD_PAGE_WIDGET_CORNER_RADIUS,
                def = 24f
            )
        )
    )
    val uiState get() = _uiState

    fun updateEnableCustomNfcCardPage(value: Boolean) {
        repo.setBoolean(
            key = WalletKeys.ENABLE_CUSTOM_NFC_CARD_PAGE,
            value = value
        )
        _uiState.value = _uiState.value.copy(enableCustomNfcCardPage = value)
    }

    fun updateNfcCardPageBkgBlurRadius(value: Int) {
        repo.setInt(
            key = WalletKeys.NFC_CARD_PAGE_BKG_BLUR_RADIUS,
            value = value
        )
        _uiState.value = _uiState.value.copy(nfcCardPageBkgBlurRadius = value)
    }

    fun updateNfcCardPageBkgScrimLight(value: Int) {
        repo.setInt(
            key = WalletKeys.NFC_CARD_PAGE_BKG_SCRIM_LIGHT,
            value = value
        )
        _uiState.value = _uiState.value.copy(nfcCardPageBkgScrimLight = value)
    }

    fun updateNfcCardPageBkgScrimDark(value: Int) {
        repo.setInt(
            key = WalletKeys.NFC_CARD_PAGE_BKG_SCRIM_DARK,
            value = value
        )
        _uiState.value = _uiState.value.copy(nfcCardPageBkgScrimDark = value)
    }

    fun updateNfcCardPageWidgetSquircle(value: Boolean) {
        repo.setBoolean(
            key = WalletKeys.NFC_CARD_PAGE_WIDGET_SQUIRCLE,
            value = value
        )
        _uiState.value = _uiState.value.copy(nfcCardPageWidgetSquircle = value)
    }

    fun updateNfcCardPageWidgetAlpha(value: Float) {
        repo.setFloat(
            key = WalletKeys.NFC_CARD_PAGE_WIDGET_ALPHA,
            value = value
        )
        _uiState.value = _uiState.value.copy(nfcCardPageWidgetAlpha = value)
    }

    fun updateNfcCardPageWidgetCornerRadius(value: Float) {
        repo.setFloat(
            key = WalletKeys.NFC_CARD_PAGE_WIDGET_CORNER_RADIUS,
            value = value
        )
        _uiState.value = _uiState.value.copy(nfcCardPageWidgetCornerRadius = value)
    }

}