package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.SystemUIKeys
import com.gustate.mcga.data.state.SystemUIUiState

class SystemUIViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = XposedRepo(context)

    private val _uiState = mutableStateOf(
        value = SystemUIUiState(
            enableCustomQsDetail = repo.getBoolean(
                key = SystemUIKeys.ENABLE_CUSTOM_QS_DETAIL,
                def = false
            ),
            qsDetailBkgCoverColor = repo.getInt(
                key = SystemUIKeys.QS_DETAIL_BKG_COVER_COLOR,
                def = 0X80000000.toInt()
            ),
            qsDetailFrgCoverColor = repo.getInt(
                key = SystemUIKeys.QS_DETAIL_FRG_COVER_COLOR,
                def = 0X80000000.toInt()
            ),
            qsDetailBkgBlurRadius = repo.getInt(
                key = SystemUIKeys.QS_DETAIL_BKG_BLUR_RADIUS,
                def = 255
            ),
            qsDetailBkgCornerRadius = repo.getFloat(
                key = SystemUIKeys.QS_DETAIL_BKG_CORNER_RADIUS,
                def = 28.0f
            ),
            enableCustomQsResizeableTile = repo.getBoolean(
                key = SystemUIKeys.ENABLE_CUSTOM_QS_RESIZEABLE_TILE,
                def = false
            ),
            qsResizeableTileCornerRadius = repo.getFloat(
                key = SystemUIKeys.QS_RESIZEABLE_TILE_CORNER_RADIUS,
                def = 28.0f
            ),
            enableAodPanoramicAllDay = repo.getBoolean(
                key = SystemUIKeys.ENABLE_AOD_PANORAMIC_ALL_DAY,
                def = false
            )
        )
    )
    val uiState: MutableState<SystemUIUiState> get() = _uiState

    fun updateEnableCustomQsDetail(enabled: Boolean) {
        repo.setBoolean(
            key = SystemUIKeys.ENABLE_CUSTOM_QS_DETAIL,
            value = enabled
        )
        _uiState.value = _uiState.value.copy(enableCustomQsDetail = enabled)
    }

    fun updateQsDetailBkgCoverColor(value: Int) {
        repo.setInt(
            key = SystemUIKeys.QS_DETAIL_BKG_COVER_COLOR,
            value = value
        )
        _uiState.value = _uiState.value.copy(qsDetailBkgCoverColor = value)
    }

    fun updateQsDetailFrgCoverColor(value: Int) {
        repo.setInt(
            key = SystemUIKeys.QS_DETAIL_FRG_COVER_COLOR,
            value = value
        )
        _uiState.value = _uiState.value.copy(qsDetailFrgCoverColor = value)
    }

    fun updateQsDetailBkgBlurRadius(value: Int) {
        repo.setInt(
            key = SystemUIKeys.QS_DETAIL_BKG_BLUR_RADIUS,
            value = value
        )
        _uiState.value = _uiState.value.copy(qsDetailBkgBlurRadius = value)
    }

    fun updateQsDetailBkgCornerRadius(value: Float) {
        repo.setFloat(
            key = SystemUIKeys.QS_DETAIL_BKG_CORNER_RADIUS,
            value = value
        )
        _uiState.value = _uiState.value.copy(qsDetailBkgCornerRadius = value)
    }

    fun updateEnableCustomQsResizeableTile(value: Boolean) {
        repo.setBoolean(
            key = SystemUIKeys.ENABLE_CUSTOM_QS_RESIZEABLE_TILE,
            value = value
        )
        _uiState.value = _uiState.value.copy(enableCustomQsResizeableTile = value)
    }

    fun updateQsResizeableTileCornerRadius(value: Float) {
        repo.setFloat(
            key = SystemUIKeys.QS_RESIZEABLE_TILE_CORNER_RADIUS,
            value = value
        )
        _uiState.value = _uiState.value.copy(qsResizeableTileCornerRadius = value)
    }

    fun updateEnableAodPanoramicAllDay(value: Boolean) {
        repo.setBoolean(
            key = SystemUIKeys.ENABLE_AOD_PANORAMIC_ALL_DAY,
            value = value
        )
        _uiState.value = _uiState.value.copy(enableAodPanoramicAllDay = value)
    }

}