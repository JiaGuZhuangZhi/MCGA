package com.gustate.mcga.panel

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gustate.mcga.R
import com.gustate.mcga.data.viewmodel.SearchViewModel
import com.gustate.mcga.ui.page.BasePanelPage
import com.gustate.mcga.ui.widget.SplicedColumnGroup
import com.gustate.mcga.ui.widget.SwitchWidget
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchPanel(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollState = rememberScrollState()
    val viewModel = viewModel<SearchViewModel>()
    val uiState by viewModel.uiState
    BasePanelPage(
        title = stringResource(R.string.hook_search),
        onBackClick = onBack,
        onRestartClick = {
            try {
                Runtime.getRuntime().exec("su -c pkill -f com.heytap.quicksearchbox")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        sharedKey = "search",
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    ) { paddingValues, scrollBehavior, hazeState ->
        Column(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            SplicedColumnGroup(
                modifier = Modifier,
                title = stringResource(id = R.string.app_suggestions),
                content = listOf(
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.receipt_long_off),
                            title = stringResource(id = R.string.hide_advice_app_name),
                            checked = uiState.hideRecomAppName,
                            onCheckedChange = { checked ->
                                viewModel.updateHideRecomAppName(checked)
                            }
                        )
                    },
                    {
                        SwitchWidget(
                            painter = painterResource(id = R.drawable.height),
                            title = stringResource(id = R.string.adjust_advice_app_card_height),
                            checked = uiState.fixRecomCardHeight,
                            onCheckedChange = { checked ->
                                viewModel.updateFixRecomCardHeight(checked)
                            }
                        )
                    }
                )
            )
        }
    }
}