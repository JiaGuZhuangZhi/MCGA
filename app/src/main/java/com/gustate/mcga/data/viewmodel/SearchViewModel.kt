package com.gustate.mcga.data.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.gustate.mcga.data.XposedRepo
import com.gustate.mcga.data.keys.SearchKeys
import com.gustate.mcga.data.state.SearchUiState

class SearchViewModel(context: Application) : AndroidViewModel(context) {
    private val repo = XposedRepo(context)

    private val _uiState = mutableStateOf(
        value = SearchUiState(
            hideRecomAppName = repo.getBoolean(SearchKeys.HIDE_RECOM_APP_NAME),
            fixRecomCardHeight = repo.getBoolean(SearchKeys.FIX_RECOM_CARD_HEIGHT)
        )
    )
    val uiState: MutableState<SearchUiState> = _uiState

    fun updateHideRecomAppName(enabled: Boolean) {
        repo.setBoolean(SearchKeys.HIDE_RECOM_APP_NAME, enabled)
        _uiState.value = _uiState.value.copy(hideRecomAppName = enabled)
    }

    fun updateFixRecomCardHeight(enabled: Boolean) {
        repo.setBoolean(SearchKeys.FIX_RECOM_CARD_HEIGHT, enabled)
        _uiState.value = _uiState.value.copy(fixRecomCardHeight = enabled)
    }
}